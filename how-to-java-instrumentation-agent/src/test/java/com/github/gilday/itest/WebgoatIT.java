package com.github.gilday.itest;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;

import com.github.gilday.stringcount.StringCountGauge;
import com.github.gilday.stringcount.StringCountGaugeMXBean;
import com.github.gilday.junit.Endpoint;
import com.github.gilday.junit.WebgoatContainerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test for a jetty application
 */
@ExtendWith(WebgoatContainerExtension.class)
class WebgoatIT {

    @Test void it_instruments_webgoat(final Endpoint endpoint, final MBeanServerConnection mBeanServerConnection) {
        assertThat(endpoint.isListening()).isTrue();
        final StringCountGaugeMXBean stringCountGaugeMXBean = MBeanServerInvocationHandler.newProxyInstance(mBeanServerConnection, StringCountGauge.name(), StringCountGaugeMXBean.class, true);

        assertThat(stringCountGaugeMXBean.created()).isGreaterThan(0);
    }
}
