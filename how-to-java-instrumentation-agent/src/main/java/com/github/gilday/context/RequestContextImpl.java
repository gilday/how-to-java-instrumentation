package com.github.gilday.context;

import java.util.HashMap;
import java.util.Map;

import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.Symbol;

/**
 * Implementation of {@link RequestContext} which keeps data in memory
 */
class RequestContextImpl implements RequestContext {

    private final Map<Symbol<?>, Object> map = new HashMap<>();

    @SuppressWarnings("unchecked") // Type-safe so long as only put adds values to the map
    @Override
    public <T> T get(final Symbol<T> key) {
        return (T) map.get(key);
    }

    @Override
    public <T> void put(final Symbol<T> key, final T value) {
        map.put(key, value);
    }
}
