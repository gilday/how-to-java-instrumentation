package com.github.gilday;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static net.bytebuddy.matcher.ElementMatchers.none;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import org.pmw.tinylog.Logger;

public class Agent {

    public static void premain(final String args, final Instrumentation instrumentation) {
        Logger.info("PRE-MAIN");
        registerMBean();

        new AgentBuilder.Default()
            .ignore(none())
            .disableClassFormatChanges()
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
            .type(is(String.class))
            .transform((builder, typeDescription, classLoader, javaModule) ->
                builder.visit(Advice.to(LogAdvice.class).on(MethodDescription::isConstructor))
            )
            .installOn(instrumentation);
    }

    private static void registerMBean() {
        final ObjectName name = StringCountGauge.name();
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            if (server.isRegistered(name)) {
                server.unregisterMBean(name);
            }
            server.registerMBean(new StringCountGauge(), StringCountGauge.name());
        } catch (InstanceNotFoundException | InstanceAlreadyExistsException | NotCompliantMBeanException | MBeanRegistrationException e) {
            throw new AgentException("Unable to register String Count MXBean", e);
        }
    }
}
