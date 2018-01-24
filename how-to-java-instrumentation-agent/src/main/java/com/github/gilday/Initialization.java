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
import com.github.gilday.stringcount.RequestContextAwareCounter;
import com.github.gilday.stringcount.StringCountGauge;
import com.github.gilday.stringcount.StringCountGaugeMXBean;
import com.github.gilday.stringcount.SimpleCounter;
import com.github.gilday.stringcount.StringsAllocatedRecordStore;
import com.google.common.eventbus.EventBus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * initializes services needed for how-to-java-instrumentation-agent
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class Initialization {

    static void initialize() {
        configureServiceLocator();
        final StringCountGauge gauge = new StringCountGauge(ServiceLocator.counter);
        registerMBean(gauge);
    }

    /**
     * wires dependencies and configures the global {@link ServiceLocator}
     */
    private static void configureServiceLocator() {
        final EventBus eventBus = new EventBus();
        final StringsAllocatedRecordStore store = new StringsAllocatedRecordStore();
        final ThreadLocalRequestContextManager ctxManager = new ThreadLocalRequestContextManager(eventBus);
        final RequestContextAwareCounter counter = new RequestContextAwareCounter(SimpleCounter::new, ctxManager, Clock.systemUTC(), store);
        eventBus.register(counter);

        ServiceLocator.requestContextManager = ctxManager;
        ServiceLocator.counter = counter;
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
            throw new InitializationException("Unable to register String Count MXBean", e);
        }
    }
}
