package com.github.gilday.context;

import com.github.gilday.bootstrap.context.RequestContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Event triggered when a {@link RequestContext} closes
 */
@Accessors(fluent = true)
@Getter
@RequiredArgsConstructor(staticName = "of")
public class RequestContextClosedEvent {
    private final RequestContext ctx;
}
