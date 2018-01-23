package com.github.gilday;

import com.github.gilday.hooks.Counter;
import com.github.gilday.hooks.ServiceLocator;
import net.bytebuddy.asm.Advice.OnMethodExit;

/**
 * Increments the {@link Counter} registered in the {@link ServiceLocator}
 */
class StringCounterAdvice {
    @OnMethodExit
    static void exit() {
        System.out.println("incrementing counter");
        ServiceLocator.stringCounter.inc();
        System.out.println("incremented counter");
    }
}
