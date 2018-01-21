package com.github.gilday.junit;

import static com.github.dockerjava.api.model.AccessMode.ro;
import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.google.common.net.InetAddresses;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * Launches instrumented WebGoat java web application in a container. Shuts down the container after the test concludes
 */
public class WebgoatContainerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private static final String KEY_CONTAINER_ID = "CONTAINER-ID";
    private static final String KEY_ENDPOINT = "ENDPOINT";

    private final DockerClient docker = DockerClientBuilder.getInstance().build();

    @Override
    public void beforeTestExecution(final ExtensionContext ctx) {
        // PULL CONTAINER IF NEEDED
        final String image = "webgoat/webgoat-7.1";
        try {
            docker.inspectImageCmd(image).exec();
        } catch (final NotFoundException e) {
            docker.pullImageCmd(image).exec(new PullImageResultCallback()).awaitSuccess();
        }

        // CREATE CONTAINER
        final Volume volume = new Volume("/agent.jar");
        final ExposedPort exposedPort = ExposedPort.tcp(8080);
        final Ports ports = new Ports();
        ports.bind(exposedPort, Ports.Binding.empty());

        final CreateContainerCmd cmd = docker.createContainerCmd(image)
            .withEntrypoint("java")
            .withCmd("-javaagent:/agent.jar", "-jar", "/webgoat.jar")
            .withExposedPorts(exposedPort)
            .withPortBindings(ports)
            .withBinds(new Bind(System.getProperty("how-to-java-instrument-jar"), volume, ro));
        final CreateContainerResponse container = cmd.exec();
        final String id = container.getId();
        docker.startContainerCmd(id).exec();

        // STORE ID IN TEST CONTEXT
        store(ctx).put(KEY_CONTAINER_ID, id);

        // GET ENDPOINT
        final InspectContainerResponse inspect = docker.inspectContainerCmd(id).exec();
        final Ports.Binding[] bindings = inspect.getNetworkSettings().getPorts().getBindings().get(exposedPort);
        if (bindings.length != 1) {
            throw new TestFrameworkException("Expected exactly one port binding");
        }
        final Endpoint endpoint = Endpoint.of(
            InetAddresses.forString(bindings[0].getHostIp()),
            Integer.valueOf(bindings[0].getHostPortSpec())
        );
        store(ctx).put(KEY_ENDPOINT, endpoint);

        // WAIT FOR SOCKET LISTEN
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        runAsync(() -> {
            while (!endpoint.isListening()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, executor);
        executor.shutdown();
        try {
            final boolean termination = executor.awaitTermination(20, TimeUnit.SECONDS);
            if (!termination) {
                throw new TestFrameworkException("Timeout waiting for container to listen at " + endpoint);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void afterTestExecution(final ExtensionContext ctx) {
        final String id = store(ctx).get(KEY_CONTAINER_ID, String.class);
        if (id != null) {
            docker.stopContainerCmd(id).exec();
            docker.removeContainerCmd(id).exec();
        }
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterCtx, final ExtensionContext ctx) throws ParameterResolutionException {
        return parameterCtx.getParameter().getType().equals(Endpoint.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterCtx, final ExtensionContext ctx) throws ParameterResolutionException {
        return store(ctx).get(KEY_ENDPOINT, Endpoint.class);
    }

    private ExtensionContext.Store store(ExtensionContext ctx) {
        return ctx.getStore(Namespace.create(getClass(), ctx));
    }
}
