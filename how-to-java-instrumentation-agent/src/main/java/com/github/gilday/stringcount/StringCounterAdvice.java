package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.ServiceLocator;
import com.github.gilday.bootstrap.stringcount.Counter;
import net.bytebuddy.asm.Advice.OnMethodEnter;

/**
 * Increments the {@link Counter} registered in the {@link ServiceLocator}
 */
public class StringCounterAdvice {
    @OnMethodEnter public static void exit() {
        ServiceLocator.counter.inc();
    }
}
