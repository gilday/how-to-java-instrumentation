package com.github.gilday;

/**
 * Generic runtime exception thrown by how-to-java-instrument agent
 */
public class AgentException extends RuntimeException {

    public AgentException(final String message, final Throwable inner) {
        super(message, inner);
    }

    public AgentException(final String message) {
        super(message);
    }
}
