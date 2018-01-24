package com.github.gilday.itest;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;

import javax.management.JMX;
import javax.management.MBeanServerConnection;

import com.github.gilday.AgentException;
import com.github.gilday.junit.Endpoint;
import com.github.gilday.junit.WebgoatContainerExtension;
import com.github.gilday.stringcount.jmx.StringsAllocatedBean;
import com.github.gilday.stringcount.jmx.StringsAllocatedGauge;
import com.github.gilday.stringcount.jmx.StringsAllocatedGaugeMXBean;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test for a jetty application
 */
@ExtendWith(WebgoatContainerExtension.class)
class WebgoatIT {

    @Test
    void it_instruments_webgoat_with_system_wide_string_allocation_count(final MBeanServerConnection mBeanServerConnection) {
        final StringsAllocatedGaugeMXBean stringCountGaugeMXBean = JMX.newMXBeanProxy(mBeanServerConnection, StringsAllocatedGauge.name(), StringsAllocatedGaugeMXBean.class, true);
        assertThat(stringCountGaugeMXBean.allocated()).isGreaterThan(0);
    }

    @Test
    void it_instruments_webgoat_with_per_request_string_allocation_count(final Endpoint endpoint, final MBeanServerConnection mBeanServerConnection) throws InterruptedException {
        // GIVEN a server that has not yet served any requests
        final StringsAllocatedGaugeMXBean stringsAllocatedGaugeMXBean = JMX.newMXBeanProxy(mBeanServerConnection, StringsAllocatedGauge.name(), StringsAllocatedGaugeMXBean.class);
        assumeTrue(stringsAllocatedGaugeMXBean.requests().length == 0);

        // WHEN make an HTTP request to the server
        final HttpUrl url = new HttpUrl.Builder()
            .scheme("http")
            .host(endpoint.host().getHostName())
            .port(endpoint.port())
            .addPathSegment("WebGoat")
            .build();
        httpGET(url);

        // AND sleep a bit to make sure the server registers the count after serving the response
        sleep(2000);

        // THEN server records a strings allocation count for the initial request and redirect to login page
        final StringsAllocatedBean[] requests = stringsAllocatedGaugeMXBean.requests();
        assertThat(requests).hasSize(2);
        // AND surely some strings were allocated to serve the requests
        assertThat(requests).allMatch(r -> r.getCount() > 0);
    }

    private static void httpGET(final HttpUrl url) {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
            .url(url)
            .get()
            .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            throw new AgentException("failed to make http request to container under test", e);
        }
    }
}
