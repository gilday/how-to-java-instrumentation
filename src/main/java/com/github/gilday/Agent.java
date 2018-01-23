package com.github.gilday;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.none;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.github.gilday.hooks.ServiceLocator;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.pmw.tinylog.Logger;

/**
 * how-to-java-instrument agent entry point
 */
public class Agent {

    public static void premain(final String args, final Instrumentation instrumentation) {
        configureServiceLocator();
        instrumentClasses(instrumentation);
        registerMBean(new StringCountGauge(ServiceLocator.stringCounter));
        Logger.info("how-to-java-instrumentation loaded");
    }

    /**
     * wires dependencies and configures the global {@link ServiceLocator}
     */
    private static void configureServiceLocator() {
        ServiceLocator.stringCounter = new LongAdderCounter();
    }

    /**
     * Uses Byte Buddy to instrument String constructors with counters.
     *
     * <p>Injects classes from package com.github.gilday.hooks into the bootstrap class loader because these classes are
     * needed while instrumenting java classes loaded in the bootstrap classloader.</p>
     *
     * <p>Instruments String constructors with counting logic</p>
     */
    private static void instrumentClasses(final Instrumentation instrumentation) {
//        final File temp;
//        try {
//            temp = Files.createTempDirectory("tmp").toFile();
//        } catch (IOException e) {
//            throw new InitializationException("Failed to create temporary file needed to bootstrap classes", e);
//        }
//        ClassInjector.UsingInstrumentation.of(temp, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, instrumentation)
//            .inject(
//                Stream.of(Counter.class, ServiceLocator.class)
//                    .collect(Collectors.toMap(
//                        TypeDescription.ForLoadedType::new,
//                        type -> ClassFileLocator.ForClassLoader.read(type).resolve()
//                    ))
//            );
        new AgentBuilder.Default()
            .ignore(none())
            .disableClassFormatChanges()
//            .enableBootstrapInjection(instrumentation, temp)
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
            .type(is(String.class))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .include(StringCounterAdvice.class.getClassLoader())
                .advice(isConstructor(), StringCounterAdvice.class.getName())
            )
//            .transform((builder, typeDescription, classLoader, javaModule) ->
//                builder.visit(Advice
//                    .to(StringCounterAdvice.class)
//                    .on(MethodDescription::isConstructor)
//                )
//            )
            .installOn(instrumentation);
    }

    /**
     * Register JMX MBeans to expose Agent metrics to external management clients
     */
    private static void registerMBean(final StringCountGaugeMXBean counter) {
        final ObjectName name = StringCountGauge.name();
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            if (server.isRegistered(name)) {
                server.unregisterMBean(name);
            }
            server.registerMBean(counter, StringCountGauge.name());
        } catch (InstanceNotFoundException | InstanceAlreadyExistsException | NotCompliantMBeanException | MBeanRegistrationException e) {
            throw new AgentException("Unable to register String Count MXBean", e);
        }
    }
}
