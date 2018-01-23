package com.github.gilday.bootstrap;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <a href="https://martinfowler.com/articles/injection.html#UsingAServiceLocator">Service Locator</a> for
 * instrumentation hooks to retrieve singleton instances which are shared across the JVM.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceLocator {
    public static Counter stringCounter;
}
