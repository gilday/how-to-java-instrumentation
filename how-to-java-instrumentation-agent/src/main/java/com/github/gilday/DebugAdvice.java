package com.github.gilday;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DebugAdvice {

    @OnMethodEnter
    static void enter(@This Object instance) {
        System.out.println("start: instrumented class " + instance.getClass().getName());
    }

    @OnMethodExit
    static void exit(@This Object instance) {
        System.out.println("stop: instrumented class " + instance.getClass().getName());
    }
}
