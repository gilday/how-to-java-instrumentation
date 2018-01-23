package com.github.gilday;

import com.github.gilday.bootstrap.Counter;
import com.github.gilday.bootstrap.ServiceLocator;
import net.bytebuddy.asm.Advice.OnMethodExit;

/**
 * Increments the {@link Counter} registered in the {@link ServiceLocator}
 */
class StringCounterAdvice {
    @OnMethodExit
    static void exit() {
        ServiceLocator.stringCounter.inc();
    }
}
