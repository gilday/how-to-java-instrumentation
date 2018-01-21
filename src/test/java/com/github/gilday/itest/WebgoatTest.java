package com.github.gilday.itest;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.gilday.junit.Endpoint;
import com.github.gilday.junit.WebgoatContainerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test for a jetty application
 */
@ExtendWith(WebgoatContainerExtension.class)
class WebgoatTest {

    @Test void it_instruments_webgoat(final Endpoint endpoint) {
        assertThat(endpoint.isListening()).isTrue();
    }
}
