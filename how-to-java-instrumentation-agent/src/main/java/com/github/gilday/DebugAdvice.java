package com.github.gilday;

import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;
import net.bytebuddy.asm.Advice.This;

public class DebugAdvice {

    private DebugAdvice() { }

    @OnMethodEnter
    static void enter(@This Object instance) {
        System.out.println("start: instrumented class " + instance.getClass().getName());
    }

    @OnMethodExit
    static void exit(@This Object instance) {
        System.out.println("stop: instrumented class " + instance.getClass().getName());
    }
}
