package com.github.gilday;

import java.lang.instrument.Instrumentation;

import org.pmw.tinylog.Logger;

public class Agent {

    public static void premain(final String args, final Instrumentation instrumentation) {
        Logger.info("PRE-MAIN");
    }

    public static void agentmain(final String args, final Instrumentation instrumentation) {
        Logger.info("AGENT-MAIN");
    }
}
