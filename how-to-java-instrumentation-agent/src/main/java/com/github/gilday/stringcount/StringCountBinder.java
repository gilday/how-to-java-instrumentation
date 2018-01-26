package com.github.gilday.stringcount;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.stringcount.jmx.StringsAllocatedGauge;
import com.google.common.eventbus.EventBus;
import lombok.RequiredArgsConstructor;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.threeten.bp.Clock;

/**
 * Provides {@link Counter} singleton and MXBean
 */
public class StringCountBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bindAsContract(StringsAllocatedRecordStore.class);
        bindFactory(CounterFactory.class).to(Counter.class).in(Singleton.class);
        bindAsContract(StringsAllocatedGauge.class);
    }

    @RequiredArgsConstructor(onConstructor = @__(@Inject))
    public static class CounterFactory implements Factory<Counter> {

        private final Clock clock;
        private final EventBus bus;
        private final StringsAllocatedRecordStore store;

        @Override
        public Counter provide() {
            new SimpleCounter(); // 🤷‍♂️ the SimpleCounter factory on the next line (implemented as a method references to nullary ctor) will crash the JVM unless SimpleCounter is first instantiated
            final RequestContextAwareCounter counter = new RequestContextAwareCounter(SimpleCounter::new, null, clock, store);
            bus.register(counter);
            return counter;
        }

        @Override
        public void dispose(final Counter counter) {
            bus.unregister(counter);
        }
    }
}
