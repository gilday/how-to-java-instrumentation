package com.github.gilday.context;

import com.github.gilday.bootstrap.context.RequestContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Event triggered when a {@link RequestContext} is created
 */
@Accessors(fluent = true)
@Getter
@RequiredArgsConstructor(staticName = "of")
public class RequestContextCreatedEvent {
    private final RequestContext ctx;
}
