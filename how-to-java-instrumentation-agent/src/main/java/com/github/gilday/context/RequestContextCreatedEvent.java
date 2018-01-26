package com.github.gilday.context;

import com.github.gilday.bootstrap.context.RequestContext;

/**
 * Event triggered when a {@link RequestContext} is created
 */
public class RequestContextCreatedEvent {

    public static RequestContextCreatedEvent of(final RequestContext ctx) { return new RequestContextCreatedEvent(ctx); }

    private final RequestContext ctx;

    private RequestContextCreatedEvent(final RequestContext ctx) {
        this.ctx = ctx;
    }

    public RequestContext ctx() {
        return this.ctx;
    }
}
