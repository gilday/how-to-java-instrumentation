package com.github.gilday.itest;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;

import javax.management.JMX;
import javax.management.MBeanServerConnection;

import com.github.gilday.AgentException;
import com.github.gilday.junit.Endpoint;
import com.github.gilday.junit.Java8;
import com.github.gilday.junit.Java8ContainersTest;
import com.github.gilday.junit.ServletContainersTest;
import com.github.gilday.junit.ServletContainersTestTemplateInvocationContextProvider;
import com.github.gilday.stringcount.jmx.StringsAllocatedBean;
import com.github.gilday.stringcount.jmx.StringsAllocatedGauge;
import com.github.gilday.stringcount.jmx.StringsAllocatedGaugeMXBean;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration test for instrumenting a Java web application with String allocation counting
 */
@ExtendWith(ServletContainersTestTemplateInvocationContextProvider.class)
class StringCountIT {

    @Java8ContainersTest
    void it_records_system_wide_string_allocation_count(final MBeanServerConnection mBeanServerConnection) {
        final StringsAllocatedGaugeMXBean stringCountGaugeMXBean = JMX.newMXBeanProxy(mBeanServerConnection, StringsAllocatedGauge.name(), StringsAllocatedGaugeMXBean.class, true);
        assertThat(stringCountGaugeMXBean.allocated()).isGreaterThan(0);
    }

    @Java8ContainersTest
    void it_records_per_request_string_allocation_count(final Endpoint endpoint, @ServletContainersTest.Context final String context, final MBeanServerConnection mBeanServerConnection) throws InterruptedException {
        // GIVEN a server that has not yet served any requests
        final StringsAllocatedGaugeMXBean stringsAllocatedGaugeMXBean = JMX.newMXBeanProxy(mBeanServerConnection, StringsAllocatedGauge.name(), StringsAllocatedGaugeMXBean.class);
        assumeTrue(stringsAllocatedGaugeMXBean.requests().length == 0);
        sleep(4000); // sleep a bit to make sure the web server is ready to serve requests

        // WHEN make an HTTP request to the server
        final HttpUrl url = new HttpUrl.Builder()
            .scheme("http")
            .host(endpoint.host().getHostName())
            .port(endpoint.port())
            .addPathSegment(context)
            .build();
        httpGET(url);

        // AND sleep a bit to make sure the server registers the count after serving the response
        sleep(200);

        // THEN server records a strings allocation count for the request
        final StringsAllocatedBean[] requests = stringsAllocatedGaugeMXBean.requests();
        assertThat(requests).hasSize(1);
        // AND surely some strings were allocated to serve the requests
        assertThat(requests).allMatch(r -> r.getCount() > 0);
    }

    private static void httpGET(final HttpUrl url) {
        final OkHttpClient client = new OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .build();
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
