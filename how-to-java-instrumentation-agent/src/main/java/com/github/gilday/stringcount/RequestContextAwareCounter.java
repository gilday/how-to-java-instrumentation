package com.github.gilday.stringcount;

import java.time.Clock;

import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.context.Symbol;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.context.RequestContextClosedEvent;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import org.pmw.tinylog.Logger;

/**
 * Decorates a {@link Counter} such that only strings created within a given {@link RequestContext} will be counted
 */
@RequiredArgsConstructor
public class RequestContextAwareCounter implements Counter {

    @VisibleForTesting final Symbol<Counter> key = Symbol.of("counter");

    private final CounterFactory factory;
    private final RequestContextManager ctxManager;
    private final Clock clock;
    private final StringsAllocatedRecordStore store;

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
    public long get() {
        final RequestContext ctx = ctxManager.get();
        if (ctx == null) {
            throw new IllegalStateException("Cannot retrieve counter value because no context registered");
        }
        return lazyGet(ctx).get();
    }

    @Subscribe public void onRequestContextClosed(final RequestContextClosedEvent event) {
        final Counter counter = event.ctx().get(key);
        // counter can be null because it is instantiated lazily
        final int count = counter == null
            ? 0
            : (int) counter.get();
        store.add(StringsAllocated.of(clock.instant(), count));
    }

    /**
     * gets (and if necessary, lazily creates) the counter in the given context
     */
    private synchronized Counter lazyGet(final RequestContext ctx) {
        Counter counter = ctx.get(key);
        if (counter == null) {
            counter = factory.create();
            ctx.put(key, counter);
        }
        return counter;
    }
}
