package com.github.gilday.stringcount;

/**
 * Gauge which returns the current "Strings created" count
 */
public interface StringCountGaugeMXBean {
    /**
     * @return number of {@link java.lang.String} instances created
     */
    long created();
}
