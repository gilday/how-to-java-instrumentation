package com.github.gilday.stringcount;

import lombok.Value;
import lombok.experimental.Accessors;
import org.threeten.bp.Instant;

/**
 * Record of the number of strings allocated in a given user request
 */
@Accessors(fluent = true)
@Value(staticConstructor = "of")
public class StringsAllocated {
    private final Instant timestamp;
    private final int count;
}
