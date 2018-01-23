package com.github.gilday.bootstrap.stringcount;

/**
 * A monotonically increasing counter metric
 */
public interface Counter {
    /**
     * increments the counter by one
     */
    void inc();

    /**
     * @return the current value of the counter
     */
    long get();
}
