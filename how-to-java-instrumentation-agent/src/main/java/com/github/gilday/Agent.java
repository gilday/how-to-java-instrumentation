package com.github.gilday;

import static net.bytebuddy.matcher.ElementMatchers.hasMethodName;
import static net.bytebuddy.matcher.ElementMatchers.hasSuperType;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.nameEndsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.none;

import java.lang.instrument.Instrumentation;

import com.github.gilday.bootstrap.ServiceLocator;
import com.github.gilday.context.RegisterRequestContextServletAdvice;
import com.github.gilday.stringcount.StringCounterAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;

/**
 * Uses Byte Buddy to instrument java.lang.String and servlets provided by the user
 */
class Agent {

    static void run(final Instrumentation instrumentation) {
        initialize();
        instrument(instrumentation);
    }

    /**
     * Publishes to the JVM global {@link ServiceLocator} to make singletons available to instrumented code
     */
    private static void initialize() {
        final AgentServices services = DaggerAgentServices.create();
        ServiceLocator.counter = services.counter();
        ServiceLocator.requestContextManager = services.contextManager();
    }

    /**
     * Uses Byte Buddy to instrument String constructors with counters and Servlets with request context managers
     */
    private static void instrument(final Instrumentation instrumentation) {
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

    private Agent() { }
}
