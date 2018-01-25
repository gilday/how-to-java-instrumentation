package com.github.gilday.stringcount;

import java8.util.concurrent.atomic.LongAdder;

/**
 * Implementation of {@link CounterWithGauge} which increments a {@link LongAdder}. Broken! the streamsupport-atomic
 * implementation of {@link LongAdder} causes this agent's String instrumentation to stack overflow. TODO Fix
 */
public class LongAdderCounter implements CounterWithGauge {

    private final LongAdder value = new LongAdder();

    @Override
    public void inc() { value.increment(); }

    @Override
    public long sample() { return value.sum(); }
}
