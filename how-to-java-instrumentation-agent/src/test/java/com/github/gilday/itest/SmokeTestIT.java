package com.github.gilday.itest;

import static com.github.gilday.ThreadUtils.sleepOrDie;

import java.time.Duration;
import java.util.concurrent.Callable;

import com.github.gilday.TestException;
import com.github.gilday.junit.containers.Endpoint;
import com.github.gilday.junit.containers.Java6ContainersTest;
import com.github.gilday.junit.containers.ServletContainersTestTemplateInvocationContextProvider;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Smoke test for Java 6 containers which do not expose JMX on a reliable port. Simply instrument the container and make
 * sure the web port is listening
 */
@ExtendWith(ServletContainersTestTemplateInvocationContextProvider.class)
class SmokeTestIT {

    private OkHttpClient client = new OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build();

    @Java6ContainersTest
    void it_instruments_java_6_containers(final Endpoint endpoint) {
        // GIVEN a Java 6 container that has been instrumented with the how-to-java-instrumentation agent
        // EXPECT container to respond to HTTP GET without crashing
        retry(5, Duration.ofSeconds(2), () -> {
            final HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(endpoint.host().getHostName())
                .port(endpoint.port())
                .build();
            final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
            client.newCall(request).execute();
            return null;
        });
    }

    private static <T> void retry(final int maxRetries, final Duration backoff, Callable<T> callable) {
        int attempts = 0;
        while (true) {
            attempts++;
            try {
                callable.call();
                return;
            } catch (Exception e) {
                if (attempts > maxRetries) {
                    throw new TestException("Max retries exceeded", e);
                }
                sleepOrDie(backoff);
            }
        }
    }
}
