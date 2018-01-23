package com.github.gilday.bootstrap.context;

/**
 * Side-effect functions to create and manage a {@link RequestContext} in global state
 */
public interface RequestContextManager {

    void create();

    void close();

    RequestContext get();
}
