package com.github.gilday.context;

import com.github.gilday.AgentException;
import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.RequestContextManager;

/**
 * {@link RequestContextManager} which stores {@link RequestContext} in {@link ThreadLocal} storage
 */
public class ThreadLocalRequestContextManager implements RequestContextManager {

    private final ThreadLocal<RequestContext> store = new ThreadLocal<>();

    @Override
    public void create() {
        if (store.get() != null) {
            throw new AgentException("Cannot create new request context because this thread already contains a request context");
        }
        final RequestContext ctx = new RequestContextImpl();
        store.set(ctx);
    }

    @Override
    public void close() {
        if (store.get() == null) {
            throw new AgentException("No context registered");
        }
        store.remove();
    }

    @Override
    public RequestContext get() {
        return store.get();
    }
}
