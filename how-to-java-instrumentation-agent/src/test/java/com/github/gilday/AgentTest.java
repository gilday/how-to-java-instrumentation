package com.github.gilday;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.gilday.stringcount.jmx.StringsAllocatedBean;
import org.junit.jupiter.api.Test;

/**
 * Tests Dagger configuration in {@link AgentModule}
 */
class AgentTest {

    @Test void simulation() {
        final Agent agent = Agent.create();

        // WHEN counter incremented outside of a context
        agent.counter.inc();
        // THEN no request recorded
        assertThat(agent.gauge.requests()).isEmpty();

        // WHEN counter incremented within a context
        agent.ctxManager.create();
        agent.counter.inc();
        agent.counter.inc();
        agent.ctxManager.close();
        // THEN request with 2 counts recorded
        assertThat(agent.gauge.requests())
            .hasSize(1)
            .extracting(StringsAllocatedBean::getCount)
            .hasOnlyOneElementSatisfying(count -> assertThat(count).isEqualTo(2));
    }
}
