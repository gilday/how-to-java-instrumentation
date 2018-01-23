package com.github.gilday;

/**
 * Generic runtime exception thrown by how-to-java-instrument agent
 */
class AgentException extends RuntimeException {
    AgentException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
