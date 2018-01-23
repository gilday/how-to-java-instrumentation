package com.github.gilday.hooks;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Service Locator for instrumentation hooks to retrieve implementations of abstractions
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceLocator {

    public static Counter stringCounter;

}
