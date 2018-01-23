package com.github.gilday;

/**
 * Thrown when the agent fails to initialize
 */
class InitializationException extends RuntimeException {

    InitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    InitializationException(final String message) {
        super(message);
    }
}
