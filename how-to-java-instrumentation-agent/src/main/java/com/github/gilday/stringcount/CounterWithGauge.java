package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.stringcount.Counter;

/**
 * Union type of {@link LongGauge} and {@link Counter}
 */
interface CounterWithGauge extends Counter, LongGauge { }
