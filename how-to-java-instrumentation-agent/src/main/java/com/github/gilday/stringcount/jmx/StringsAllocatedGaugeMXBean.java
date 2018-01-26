package com.github.gilday.stringcount.jmx;

/**
 * Gauge which returns the current "Strings allocated" count
 */
public interface StringsAllocatedGaugeMXBean {

    /**
     * @return set of {@link StringsAllocatedBean} records for each request
     */
    StringsAllocatedBean[] requests();
}
