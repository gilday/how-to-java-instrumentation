package com.github.gilday;

import net.bytebuddy.asm.Advice.OnMethodEnter;

/**
 * Hello, world Advice that logs on method exit
 */
class LogAdvice {
    @OnMethodEnter
    static void enter() {
        System.out.println("string created!");
    }
}
