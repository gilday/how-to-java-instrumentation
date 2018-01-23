package com.github.gilday.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.github.gilday.bootstrap.context.RequestContext;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ThreadLocalRequestContextManager}
 */
class ThreadLocalRequestContextManagerTest {

    private final ThreadLocalRequestContextManager manager = new ThreadLocalRequestContextManager();

    @Test
    void it_stores_request_context_in_thread_local_storage() throws InterruptedException, ExecutionException, TimeoutException {
        // create new contexts in two new threads
        final List<CompletableFuture<RequestContext>> futures = Arrays.asList(
            CompletableFuture.supplyAsync(this::createNew, Executors.newSingleThreadExecutor()),
            CompletableFuture.supplyAsync(this::createNew, Executors.newSingleThreadExecutor())
        );
        // wait for all futures
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get(1, TimeUnit.SECONDS);
        // get future responses
        final List<RequestContext> contexts = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());

        assertThat(contexts).hasSize(2);
        final RequestContext first = contexts.get(0);
        final RequestContext second = contexts.get(1);
        assertThat(first).isNotSameAs(second);
    }

    private RequestContext createNew() {
        manager.create();
        return manager.get();
    }
}
