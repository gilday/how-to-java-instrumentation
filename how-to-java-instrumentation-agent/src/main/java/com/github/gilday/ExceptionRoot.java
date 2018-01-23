package com.github.gilday;

import org.pmw.tinylog.Logger;

/**
 * Exception root for handling all possible errors in a given {@link Runnable}
 */
public class ExceptionRoot {

    public static void run(final Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            if (t instanceof Error) {
                throw (Error) t;
            }
            Logger.error(t, "how-to-java-instrumentation unexpected error");
        }
    }
}
