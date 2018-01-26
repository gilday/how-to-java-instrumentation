package com.github.gilday.stringcount.jmx;

import javax.inject.Inject;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.github.gilday.AgentException;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.stringcount.LongGauge;
import com.github.gilday.stringcount.StringsAllocatedRecordStore;
import java8.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link StringsAllocatedGaugeMXBean} which wraps a {@link Counter}
 */
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class StringsAllocatedGauge implements StringsAllocatedGaugeMXBean {

    public static ObjectName name() {
        try {
            return ObjectName.getInstance("com.github.gilday:type=StringCountGauge");
        } catch (MalformedObjectNameException e) {
            throw new AgentException("Failed to create MXBean object name", e);
        }
    }

    private final StringsAllocatedRecordStore store;

    @Override
    public StringsAllocatedBean[] requests() {
        return StreamSupport.stream(store.records())
            .map(StringsAllocatedBean::fromRecord)
            .toArray(StringsAllocatedBean[]::new);
    }
}
