package com.github.gilday.stringcount.jmx;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.github.gilday.AgentException;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.stringcount.StringsAllocatedRecordStore;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link StringsAllocatedGaugeMXBean} which wraps a {@link Counter}
 */
@RequiredArgsConstructor
public class StringsAllocatedGauge implements StringsAllocatedGaugeMXBean {

    public static ObjectName name() {
        try {
            return ObjectName.getInstance("com.github.gilday:type=StringCountGauge");
        } catch (MalformedObjectNameException e) {
            throw new AgentException("Failed to create MXBean object name", e);
        }
    }

    private final Counter counter;
    private final StringsAllocatedRecordStore store;

    @Override
    public long allocated() { return counter.get(); }

    @Override
    public StringsAllocatedBean[] requests() {
        return store.records().stream()
            .map(StringsAllocatedBean::fromRecord)
            .toArray(StringsAllocatedBean[]::new);
    }
}
