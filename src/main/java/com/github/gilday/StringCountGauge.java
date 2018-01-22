package com.github.gilday;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Implementation of {@link StringCountGaugeMXBean}
 */
public class StringCountGauge implements StringCountGaugeMXBean {

    public static ObjectName name() {
        try {
            return ObjectName.getInstance("com.github.gilday:type=StringCountGauge");
        } catch (MalformedObjectNameException e) {
            throw new AgentException("Failed to create MXBean object name", e);
        }
    }

    @Override
    public int created() {
        return 0;
    }
}
