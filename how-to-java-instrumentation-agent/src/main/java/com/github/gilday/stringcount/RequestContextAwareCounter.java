package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.context.Symbol;
import com.github.gilday.bootstrap.stringcount.Counter;
import lombok.RequiredArgsConstructor;

/**
 * Decorates a {@link Counter} such that only strings created within a given {@link RequestContext} will be counted
 */
@RequiredArgsConstructor
public class RequestContextAwareCounter implements Counter {

    private final Symbol<Counter> key = Symbol.of("counter");

    private final CounterFactory factory;
    private final RequestContextManager ctxManager;

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

    /**
     * gets (and if necessary, lazily creates) the counter in the given context
     */
    private synchronized Counter lazyGet(final RequestContext ctx) {
        Counter counter = ctx.get(key);
        if (counter == null) {
            counter = factory.create();
        }
        ctx.put(key, counter);
        return counter;
    }
}
