package com.github.gilday.stringcount;

/**
 * thread-unsafe counter simply wraps an int. Should only be used when the count will only be incremented from a single
 * thread
 */
public class SimpleCounter implements CounterWithGauge {

    private int value;

    @Override
    public void inc() { value++; }

    @Override
    public long sample() { return value; }
}
