package com.github.gilday;

import java.io.IOException;

/**
 * Thrown when the agent fails to initialize
 */
class InitializationException extends RuntimeException {

    InitializationException(final String message, final IOException inner) {
        super(message, inner);
    }
}
