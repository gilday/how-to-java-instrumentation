package com.github.gilday;

import java.time.Duration;

/**
 * static utility
 */
public class ThreadUtils {

    /**
     * Sleeps the current thread and dies if interrupted
     */
    public static void sleepOrDie(final Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private ThreadUtils() { }
}
