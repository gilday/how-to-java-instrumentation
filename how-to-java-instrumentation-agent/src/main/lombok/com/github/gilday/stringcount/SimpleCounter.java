package com.github.gilday.stringcount;

import javax.inject.Inject;

import lombok.NoArgsConstructor;

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

    @NoArgsConstructor(onConstructor = @__(@Inject))
    public static class Factory implements CounterFactory {

        @Override
        public CounterWithGauge create() {
            return new SimpleCounter();
        }
    }
}
