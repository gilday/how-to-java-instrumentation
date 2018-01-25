package com.github.gilday;

/**
 * Generic {@link RuntimeException} for tests
 */
public class TestException extends RuntimeException {

    public TestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
