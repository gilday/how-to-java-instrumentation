package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.bootstrap.ServiceLocator;
import net.bytebuddy.asm.Advice.OnMethodExit;

/**
 * Increments the {@link Counter} registered in the {@link ServiceLocator}
 */
public class StringCounterAdvice {
    @OnMethodExit public static void exit() {
        ServiceLocator.counter.inc();
    }
}
