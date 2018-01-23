package com.github.gilday.junit;

import java.time.Duration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * static utility
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ThreadUtils {

    /**
     * Sleeps the current thread and dies if interrupted
     */
    static void sleepOrDie(final Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
