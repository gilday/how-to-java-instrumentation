package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.stringcount.Counter;

/**
 * thread-unsafe counter simply wraps an int. Should only be used when the count will only be incremented from a single
 * thread
 */
public class ThreadUnsafeCounter implements Counter {

    private int value;

    @Override
    public void inc() { value++; }

    @Override
    public long get() { return value; }
}
