package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.AgentServiceLocator;
import com.github.gilday.bootstrap.stringcount.Counter;
import net.bytebuddy.asm.Advice.OnMethodEnter;

/**
 * Increments the {@link Counter} registered in the {@link AgentServiceLocator}
 */
public class StringCounterAdvice {
    @OnMethodEnter public static void exit() {
        AgentServiceLocator.counter.inc();
    }
}
