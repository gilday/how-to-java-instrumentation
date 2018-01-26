package com.github.gilday.stringcount;

import javax.inject.Inject;

/**
 * thread-unsafe counter simply wraps an int. Should only be used when the count will only be incremented from a single
 * thread
 */
public class SimpleCounter implements CounterWithGauge {

    private long value;

    @Override
    public void inc() { value++; }

    @Override
    public long sample() { return value; }

    public static class Factory implements CounterFactory {

        @Inject public Factory() { }

        @Override
        public CounterWithGauge create() {
            return new SimpleCounter();
        }
    }
}
