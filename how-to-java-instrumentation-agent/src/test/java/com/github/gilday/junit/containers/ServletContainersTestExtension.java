package com.github.gilday.junit.containers;

import static com.github.dockerjava.api.model.AccessMode.ro;
import static com.github.gilday.junit.containers.ThreadUtils.sleepOrDie;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.google.common.collect.ImmutableList;
import com.google.common.net.InetAddresses;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Launches instrumented java servlet container in a docker container. Shuts down the docker container after the test
 * concludes
 *
 * TODO externalize timeout config
 */
@RequiredArgsConstructor
class ServletContainersTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private static final String KEY_CONTAINER_ID = "CONTAINER-ID";
    private static final String KEY_ENDPOINT = "ENDPOINT";
    private static final String KEY_JMX_CONNECTOR = "JMX-CONNECTOR";
    private static final String KEY_MBEAN_SERVER_CONNECTION = "MBEAN-SERVER-CONNECTION";

    private final DockerClient docker;
    private final ServletContainerExecutionMetadata metadata;

    @Override
    public void beforeTestExecution(final ExtensionContext ctx) {
        // PULL CONTAINER IF NEEDED
        try {
            docker.inspectImageCmd(metadata.image()).exec();
        } catch (final NotFoundException e) {
            docker.pullImageCmd(metadata.image()).exec(new PullImageResultCallback()).awaitSuccess();
        }

        // CREATE CONTAINER
        final Volume volume = new Volume("/agent.jar");
        final ExposedPort exposedWebPort = ExposedPort.tcp(metadata.port());
        final ExposedPort exposedJMXPort = ExposedPort.tcp(9010);
        final Ports ports = new Ports();
        ports.bind(exposedJMXPort, Ports.Binding.bindPort(9010));
        ports.bind(exposedWebPort, Ports.Binding.empty());

        final ImmutableList<String> standardJavaOpts = ImmutableList.of(
            "-javaagent:/agent.jar",
            "-Dcom.sun.management.jmxremote.host=0.0.0.0",
            "-Dcom.sun.management.jmxremote.port=9010",
            "-Dcom.sun.management.jmxremote.authenticate=false",
            "-Dcom.sun.management.jmxremote.ssl=false",
            "-Dcom.sun.management.jmxremote.rmi.port=9010",
            "-Djava.rmi.server.hostname=0.0.0.0"
        );

        CreateContainerCmd create = docker.createContainerCmd(metadata.image())
            .withCmd(
            )
            .withEnv(metadata.javaOptsEnvVariableName() + "=" + standardJavaOpts.stream().collect(Collectors.joining(" ")))
            .withExposedPorts(exposedJMXPort, exposedWebPort)
            .withPortBindings(ports)
            .withBinds(new Bind(System.getProperty("how-to-java-instrument-jar"), volume, ro));
        final ImmutableList<String> cmd = metadata.prependJavaOptsToCmd()
            ? new ImmutableList.Builder<String>().addAll(standardJavaOpts).addAll(metadata.cmd()).build()
            : metadata.cmd();
        if (!cmd.isEmpty()) {
            create = create.withCmd(cmd);
        }
        if (!metadata.entrypoint().isEmpty()) {
            create = create.withEntrypoint(metadata.entrypoint());
        }
        final CreateContainerResponse container = create.exec();
        final String id = container.getId();
        docker.startContainerCmd(id).exec();

        // STORE ID IN TEST CONTEXT
        store(ctx).put(KEY_CONTAINER_ID, id);

        // GET JMX CONNECTION AND WEB ENDPOINT
        final InspectContainerResponse inspect = docker.inspectContainerCmd(id).exec();
        final Map<ExposedPort, Ports.Binding[]> bindings = inspect.getNetworkSettings().getPorts().getBindings();
        final Endpoint jmx = endpointFromBinding(bindings.get(exposedJMXPort));
        final Endpoint web = endpointFromBinding(bindings.get(exposedWebPort));
        store(ctx).put(KEY_ENDPOINT, web);

        // WAIT FOR SOCKET LISTEN
        try {
            CompletableFuture
                .allOf(jmx.pollForConnection(), web.pollForConnection())
                .get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new TestFrameworkException("Unable to connect to JMX and web sockets", e);
        } catch (TimeoutException e) {
            throw new TestFrameworkException("Timeout connecting to JMX and web sockets", e);
        }

        // ESTABLISH JMX CONNECTION
        final JMXServiceURL jmxURL;
        try {
            jmxURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + jmx.host().getHostName() + ":" + jmx.port() + "/jmxrmi");
        } catch (MalformedURLException e) {
            throw new TestFrameworkException("Bad JMX URL", e);
        }
        final JMXConnector connector;
        try {
            connector = CompletableFuture
                .supplyAsync(() -> {
                    // naive retry loop
                    while (true) {
                        try {
                            return JMXConnectorFactory.connect(jmxURL);
                        } catch (IOException e) {
                            // continue
                        }
                        sleepOrDie(Duration.ofSeconds(1));
                    }
                })
                .get(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TestFrameworkException("Interrupted while connecting to container JMX", e);
        } catch (ExecutionException e) {
            throw new TestFrameworkException("Error connecting to container JMX", e);
        } catch (TimeoutException e) {
            throw new TestFrameworkException("Timeout connecting to container JMX", e);
        }
        store(ctx).put(KEY_JMX_CONNECTOR, connector);
        final MBeanServerConnection mBeanServerConnection;
        try {
            mBeanServerConnection = connector.getMBeanServerConnection();
        } catch (IOException e) {
            throw new TestFrameworkException("Unable to retrieve MBeanServerConnection", e);
        }
        store(ctx).put(KEY_MBEAN_SERVER_CONNECTION, mBeanServerConnection);
    }

    @Override
    public void afterTestExecution(final ExtensionContext ctx) {
        final JMXConnector connector = store(ctx).get(KEY_JMX_CONNECTOR, JMXConnector.class);
        if (connector != null) {
            try {
                connector.close();
            } catch (IOException e) {
                throw new TestFrameworkException("Unable to close JMXConnector", e);
            }
        }
        final String id = store(ctx).get(KEY_CONTAINER_ID, String.class);
        if (id != null) {
            docker.stopContainerCmd(id).exec();
            docker.removeContainerCmd(id).exec();
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterCtx, final ExtensionContext ctx) throws ParameterResolutionException {
        final Class<?> type = parameterCtx.getParameter().getType();
        return type.equals(Endpoint.class)
            || type.equals(MBeanServerConnection.class)
            || (type.equals(String.class) && parameterCtx.getParameter().getAnnotation(ServletContainersTest.Context.class) != null);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterCtx, final ExtensionContext ctx) throws ParameterResolutionException {
        final Class<?> type = parameterCtx.getParameter().getType();
        if (type.equals(MBeanServerConnection.class)) {
            return store(ctx).get(KEY_MBEAN_SERVER_CONNECTION, MBeanServerConnection.class);
        }
        if (type.equals(Endpoint.class)) {
            return store(ctx).get(KEY_ENDPOINT, Endpoint.class);
        }
        if (type.equals(String.class) && parameterCtx.getParameter().getAnnotation(ServletContainersTest.Context.class) != null) {
            return metadata.context();
        }
        throw new IllegalArgumentException("parameter context is not supported");
    }

    private ExtensionContext.Store store(ExtensionContext ctx) {
        return ctx.getStore(Namespace.create(getClass(), ctx));
    }

    private Endpoint endpointFromBinding(final Ports.Binding[] bindings) {
        if (bindings.length != 1) {
            throw new TestFrameworkException("Expected exactly one port binding");
        }
        return Endpoint.of(
            bindings[0].getHostIp().equals("0.0.0.0")
                ? InetAddress.getLoopbackAddress()
                : InetAddresses.forString(bindings[0].getHostIp()),
            Integer.valueOf(bindings[0].getHostPortSpec())
        );
    }
}
