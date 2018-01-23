package com.github.gilday;

import java.util.concurrent.atomic.LongAdder;

import com.github.gilday.bootstrap.Counter;

/**
 * Implementation of {@link Counter} which increments a {@link LongAdder}
 */
class LongAdderCounter implements Counter {

    private final LongAdder value = new LongAdder();

    @Override
    public void inc() { value.increment(); }

    @Override
    public long get() { return value.sum(); }
}
