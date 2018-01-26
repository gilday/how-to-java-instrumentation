package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.stringcount.Counter;

/**
 * Union type of {@link LongGauge} and {@link Counter}
 */
public interface CounterWithGauge extends Counter, LongGauge { }
