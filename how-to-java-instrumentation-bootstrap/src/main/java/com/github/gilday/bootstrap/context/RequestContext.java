package com.github.gilday.bootstrap.context;

/**
 * how-to-java-instrumentation context holder for tracking state throughout a single user request
 */
public interface RequestContext {
    /**
     * get an arbitrary value from the context
     */
    <T> T get(final Symbol<T> key);

    /**
     * put an arbitrary value in the context
     */
    <T> void put(final Symbol<T> key, final T value);
}
