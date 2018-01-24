package com.github.gilday.stringcount;

import java.time.Instant;

import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Record of the number of strings allocated in a given user request
 */
@Accessors(fluent = true)
@Value(staticConstructor = "of")
public class StringsAllocated {
    private final Instant timestamp;
    private final int count;
}
