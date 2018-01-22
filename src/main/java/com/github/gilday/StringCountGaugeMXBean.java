package com.github.gilday;

/**
 * Gauge which returns the current "Strings created" count
 */
public interface StringCountGaugeMXBean {
    int created();
}
