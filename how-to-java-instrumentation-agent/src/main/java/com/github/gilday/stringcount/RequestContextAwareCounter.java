package com.github.gilday.stringcount;

import javax.inject.Inject;

import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.context.Symbol;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.context.RequestContextClosedEvent;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.Subscribe;
import org.threeten.bp.Clock;

/**
 * Decorates a {@link Counter} such that only strings created within a given {@link RequestContext} will be counted
 */
public class RequestContextAwareCounter implements CounterWithGauge {

    @VisibleForTesting final Symbol<CounterWithGauge> key = Symbol.of("counter");

    private final CounterFactory factory;
    private final RequestContextManager ctxManager;
    private final Clock clock;
    private final StringsAllocatedRecordStore store;

    @Inject
    public RequestContextAwareCounter(final CounterFactory factory, final RequestContextManager ctxManager, final Clock clock, final StringsAllocatedRecordStore store) {
        this.factory = factory;
        this.ctxManager = ctxManager;
        this.clock = clock;
        this.store = store;
    }

    @Override
    public void inc() {
        final RequestContext ctx = ctxManager.get();
        if (ctx == null) {
            // ignore increments which happen outside of a context
            return;
        }
        lazyGet(ctx).inc();
    }

    @Override
    public long sample() {
        final RequestContext ctx = ctxManager.get();
        if (ctx == null) {
            throw new IllegalStateException("Cannot retrieve counter value because no context registered");
        }
        return lazyGet(ctx).sample();
    }

    @Subscribe public void onRequestContextClosed(final RequestContextClosedEvent event) {
        final CounterWithGauge counter = event.ctx().get(key);
        // counter can be null because it is instantiated lazily
        final int count = counter == null
            ? 0
            : (int) counter.sample();
        store.add(StringsAllocated.of(clock.instant(), count));
    }

    /**
     * gets (and if necessary, lazily creates) the counter in the given context
     */
    private CounterWithGauge lazyGet(final RequestContext ctx) {
        CounterWithGauge counter = ctx.get(key);
        if (counter == null) {
            counter = factory.create();
            ctx.put(key, counter);
        }
        return counter;
    }
}
