package com.github.gilday;

import java.lang.management.ManagementFactory;
import java.time.Clock;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import com.github.gilday.bootstrap.ServiceLocator;
import com.github.gilday.context.ThreadLocalRequestContextManager;
import com.github.gilday.stringcount.LongAdderCounter;
import com.github.gilday.stringcount.MultiplexCounter;
import com.github.gilday.stringcount.RequestContextAwareCounter;
import com.github.gilday.stringcount.SimpleCounter;
import com.github.gilday.stringcount.StringsAllocatedRecordStore;
import com.github.gilday.stringcount.jmx.StringsAllocatedGauge;
import com.google.common.eventbus.EventBus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * initializes services needed for how-to-java-instrumentation-agent
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Initialization {

    static void initialize() {
        // wire dependencies
        final EventBus eventBus = new EventBus();
        final StringsAllocatedRecordStore store = new StringsAllocatedRecordStore();
        final ThreadLocalRequestContextManager ctxManager = new ThreadLocalRequestContextManager(eventBus);
        final RequestContextAwareCounter perRequestCounter = new RequestContextAwareCounter(SimpleCounter::new, ctxManager, Clock.systemUTC(), store);
        eventBus.register(perRequestCounter);
        final LongAdderCounter systemWideCounter = new LongAdderCounter();
        final MultiplexCounter counter = MultiplexCounter.of(
            perRequestCounter,
            systemWideCounter
        );

        // expose singletons to ServiceLocator
        ServiceLocator.requestContextManager = ctxManager;
        ServiceLocator.counter = counter;

        // Register JMX MBeans to expose Agent metrics to external management clients
        final StringsAllocatedGauge gauge = new StringsAllocatedGauge(systemWideCounter, store);
        registerMBean(gauge);
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
}
