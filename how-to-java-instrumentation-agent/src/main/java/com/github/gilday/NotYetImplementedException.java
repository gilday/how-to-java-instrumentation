package com.github.gilday;

/**
 * TDD tool which indicates code under test which has not yet been implemented
 */
public class NotYetImplementedException extends RuntimeException {
    public NotYetImplementedException() { super("Not yet implemented"); }
}
