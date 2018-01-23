package com.github.gilday;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.github.gilday.bootstrap.Counter;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of {@link StringCountGaugeMXBean} which wraps a {@link Counter}
 */
@RequiredArgsConstructor
public class StringCountGauge implements StringCountGaugeMXBean {

    public static ObjectName name() {
        try {
            return ObjectName.getInstance("com.github.gilday:type=StringCountGauge");
        } catch (MalformedObjectNameException e) {
            throw new AgentException("Failed to create MXBean object name", e);
        }
    }

    private final Counter counter;

    @Override
    public long created() { return counter.get(); }
}
