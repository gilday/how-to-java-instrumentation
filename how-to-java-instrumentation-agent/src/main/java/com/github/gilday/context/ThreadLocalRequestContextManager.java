package com.github.gilday.context;

import javax.inject.Inject;

import com.github.gilday.AgentException;
import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.RequestContextManager;
import com.google.common.eventbus.EventBus;
import org.pmw.tinylog.Logger;

/**
 * {@link RequestContextManager} which stores {@link RequestContext} in {@link ThreadLocal} storage
 */
class ThreadLocalRequestContextManager implements RequestContextManager {

    private final EventBus eventBus;
    private final ThreadLocal<ContextCount> store = new ThreadLocal<>();

    @Inject ThreadLocalRequestContextManager(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void create() {
        final ContextCount pair = store.get();
        if (pair != null) {
            pair.inc(); // increment nested servlet count
            return;
        }
        final RequestContext ctx = new RequestContextImpl();
        store.set(new ContextCount(ctx));
        eventBus.post(RequestContextCreatedEvent.of(ctx));
    }

    @Override
    public void close() {
        final ContextCount pair = store.get();
        if (pair == null) {
            throw new AgentException("No context registered");
        }
        pair.dec();
        if (pair.count() == 0) {
            final RequestContext ctx = pair.ctx();
            store.remove();
            eventBus.post(RequestContextClosedEvent.of(ctx));
        }
    }

    @Override
    public RequestContext get() {
        final ContextCount pair = store.get();
        return pair == null
            ? null
            : pair.ctx();
    }

    /**
     * Pair class which groups a {@link RequestContext} with a latch count for tracking nested servlets
     */
    private static class ContextCount {

        private final RequestContext ctx;
        private int count = 1;

        ContextCount(final RequestContext ctx) {
            this.ctx = ctx;
        }

        private void inc() {
            count++;
        }

        private void dec() {
            if (count == 0) {
                throw new IllegalStateException("Cannot decrement latch count because it is 0");
            }
            count--;
        }

        RequestContext ctx() {
            return this.ctx;
        }

        public int count() {
            return this.count;
        }
    }
}
