package com.github.gilday.stringcount;

import java.util.concurrent.atomic.LongAdder;

import com.github.gilday.bootstrap.stringcount.Counter;

/**
 * Implementation of {@link Counter} which increments a {@link LongAdder}
 */
public class LongAdderCounter implements CounterWithGauge {

    private final LongAdder value = new LongAdder();

    @Override
    public void inc() { value.increment(); }

    @Override
    public long sample() { return value.sum(); }
}
