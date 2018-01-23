package com.github.gilday;

import com.github.gilday.hooks.Counter;

public class NopCounter implements Counter {

    public static final Counter instance = new NopCounter();

    @Override
    public void inc() { }

    @Override
    public long get() { return 0; }
}
