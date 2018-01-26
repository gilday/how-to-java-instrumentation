package com.github.gilday.stringcount;

import com.google.auto.value.AutoValue;
import org.threeten.bp.Instant;

/**
 * Record of the number of strings allocated in a given user request
 */
@AutoValue
public abstract class StringsAllocated {

    static StringsAllocated of(final Instant timestamp, final int count) {
        return new AutoValue_StringsAllocated(timestamp, count);
    }

    public abstract Instant timestamp();
    public abstract int count();
}
