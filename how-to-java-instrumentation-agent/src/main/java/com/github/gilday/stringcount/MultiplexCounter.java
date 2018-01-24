package com.github.gilday.stringcount;

import java.util.Arrays;
import java.util.List;

import com.github.gilday.bootstrap.stringcount.Counter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Decorator which multiplexes an increment operation across a number of other {@link Counter} instances
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiplexCounter implements Counter {

    public static MultiplexCounter of(final Counter... counters) {
        return new MultiplexCounter(Arrays.asList(counters));
    }

    private final List<Counter> counters;

    @Override
    public void inc() {
        for (Counter counter : counters) {
            counter.inc();
        }
    }
}
