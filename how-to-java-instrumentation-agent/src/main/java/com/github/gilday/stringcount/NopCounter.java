package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.stringcount.Counter;

public class NopCounter implements Counter {

    @Override
    public void inc() { }

    @Override
    public long get() { return 0; }
}
