package com.github.gilday.bootstrap;

import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.stringcount.Counter;

/**
 * <a href="https://martinfowler.com/articles/injection.html#UsingAServiceLocator">Service Locator</a> for
 * instrumentation hooks to retrieve singleton instances which are shared across the JVM.
 */
public class ServiceLocator {
    public static Counter counter;
    public static RequestContextManager requestContextManager;

    private ServiceLocator() { }
}
