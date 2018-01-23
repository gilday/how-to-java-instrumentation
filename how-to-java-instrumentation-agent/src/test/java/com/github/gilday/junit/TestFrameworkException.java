package com.github.gilday.junit;

/**
 * Generic {@link RuntimeException} for failures in test framework
 */
public class TestFrameworkException extends RuntimeException {

    public TestFrameworkException(final String message) {
        super(message);
    }

    public TestFrameworkException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
