package com.github.gilday.context;

import com.github.gilday.bootstrap.context.RequestContext;

/**
 * Event triggered when a {@link RequestContext} closes
 */
public class RequestContextClosedEvent {

    public static RequestContextClosedEvent of(final RequestContext ctx) {
        return new RequestContextClosedEvent(ctx);
    }

    private final RequestContext ctx;

    private RequestContextClosedEvent(final RequestContext ctx) {
        this.ctx = ctx;
    }

    public RequestContext ctx() {
        return this.ctx;
    }
}
