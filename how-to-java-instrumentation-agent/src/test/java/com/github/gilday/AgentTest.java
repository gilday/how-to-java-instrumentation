package com.github.gilday;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests Dagger configuration in {@link AgentModule}
 */
class AgentTest {

    @Test
    void create_agent() {
        assertThat(Agent.create()).isNotNull();
    }
}
