package com.github.gilday;

import static net.bytebuddy.matcher.ElementMatchers.hasMethodName;
import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.nameEndsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.github.gilday.bootstrap.AgentServiceLocator;
import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.bus.EventBusBinder;
import com.github.gilday.clock.ClockBinder;
import com.github.gilday.context.ContextBinder;
import com.github.gilday.context.RegisterRequestContextServletAdvice;
import com.github.gilday.stringcount.StringCountBinder;
import com.github.gilday.stringcount.StringCounterAdvice;
import com.github.gilday.stringcount.jmx.StringsAllocatedGauge;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * Initializes global singletons, publishes singletons to the {@link AgentServiceLocator} to make them available to
 * instrumented code, then instruments java.lang.String and user Servlets with Byte Buddy
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Agent {

    static void run(final Instrumentation instrumentation) {
        final ServiceLocator locator = ServiceLocatorUtilities.bind(
            "how-to-java-instrumentation",
            new EventBusBinder(),
            new ClockBinder(),
            new ContextBinder(),
            new StringCountBinder()
        );

        // expose singletons to ServiceLocator
        AgentServiceLocator.requestContextManager = locator.getService(RequestContextManager.class);
        AgentServiceLocator.counter = locator.getService(Counter.class);

        // Register JMX MBeans to expose Agent metrics to external management clients
        final StringsAllocatedGauge gauge = locator.getService(StringsAllocatedGauge.class);
        registerMBean(gauge);

        // Run instrumentation
        instrumentClasses(instrumentation);
    }

    private static void registerMBean(final StringsAllocatedGauge gauge) {
        final ObjectName name = StringsAllocatedGauge.name();
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            if (server.isRegistered(name)) {
                server.unregisterMBean(name);
            }
            server.registerMBean(gauge, StringsAllocatedGauge.name());
        } catch (InstanceNotFoundException | InstanceAlreadyExistsException | NotCompliantMBeanException | MBeanRegistrationException e) {
            throw new InitializationException("Unable to register String Count MXBean", e);
        }
    }

    /**
     * Uses Byte Buddy to instrument String constructors with counters
     */
    private static void instrumentClasses(final Instrumentation instrumentation) {
        new AgentBuilder.Default()
            .ignore(none())
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
            .type(
                is(String.class)
            )
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .include(StringCounterAdvice.class.getClassLoader())
                .advice(isConstructor(), StringCounterAdvice.class.getName())
            )
            .type(
                hasSuperType(named("javax.servlet.Servlet").and(nameEndsWith("Servlet")))
            )
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .advice(hasMethodName("service"), RegisterRequestContextServletAdvice.class.getName())
            )
            .type(
                hasSuperType(named("org.eclipse.jetty.server.Handler"))
            )
            .transform(new AgentBuilder.Transformer.ForAdvice()
                .advice(hasMethodName("handle"), RegisterRequestContextServletAdvice.class.getName())
            )
            .installOn(instrumentation);
    }
}
