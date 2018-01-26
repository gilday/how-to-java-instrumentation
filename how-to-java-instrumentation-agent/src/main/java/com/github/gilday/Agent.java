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

import javax.inject.Inject;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.github.gilday.bootstrap.ServiceLocator;
import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.context.RegisterRequestContextServletAdvice;
import com.github.gilday.stringcount.StringCounterAdvice;
import com.github.gilday.stringcount.jmx.StringsAllocatedGauge;
import dagger.ObjectGraph;
import net.bytebuddy.agent.builder.AgentBuilder;

/**
 * Uses Byte Buddy to instrument java.lang.String and servlets provided by the user
 */
class Agent {

    static Agent create() { return ObjectGraph.create(new AgentModule()).get(Agent.class); }

    private final Counter counter;
    private final RequestContextManager ctxManager;
    private final StringsAllocatedGauge gauge;

    @Inject
    public Agent(final Counter counter, final RequestContextManager ctxManager, final StringsAllocatedGauge gauge) {
        this.counter = counter;
        this.ctxManager = ctxManager;
        this.gauge = gauge;
    }

    void run(final Instrumentation instrumentation) {
        // publish singletons to the JVM global ServiceLocator to make them available to instrumented code
        ServiceLocator.counter = counter;
        ServiceLocator.requestContextManager = ctxManager;

        // Register JMX MBeans to expose Agent metrics to external management clients
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

        // Use Byte Buddy to instrument String constructors with counters and Servlets with request context managers
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
