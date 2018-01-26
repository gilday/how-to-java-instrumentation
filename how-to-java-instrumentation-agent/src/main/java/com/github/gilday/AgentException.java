package com.github.gilday;

/**
 * Generic runtime exception thrown by how-to-java-instrument agent
 */
public class AgentException extends RuntimeException {

    public AgentException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AgentException(final String message) {
        super(message);
    }
}
