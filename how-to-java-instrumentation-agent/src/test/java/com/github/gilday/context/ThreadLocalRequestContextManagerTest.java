package com.github.gilday.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.github.gilday.bootstrap.context.RequestContext;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ThreadLocalRequestContextManager}
 */
class ThreadLocalRequestContextManagerTest {

    private ThreadLocalRequestContextManager manager;
    private RequestContextCreatedListener listener;

    @BeforeEach
    void before() {
        final EventBus eventBus = new EventBus();
        manager = new ThreadLocalRequestContextManager(eventBus);
        listener = new RequestContextCreatedListener();
        eventBus.register(listener);
    }

    @Test
    void it_stores_request_context_in_thread_local_storage() throws InterruptedException, ExecutionException, TimeoutException {
        // GIVEN create new contexts in two distinct threads
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

        // EXPECT two distinct contexts created
        assertThat(contexts)
            .hasSize(2)
            .allMatch(Objects::nonNull);
        final RequestContext first = contexts.get(0);
        final RequestContext second = contexts.get(1);
        assertThat(first).isNotSameAs(second);

        // AND RequestContextCreatedEvent triggered twice
        assertThat(listener.created).isEqualTo(2);
    }

    @Test
    void it_closes_after_all_nested_servlets_finish() {
        // GIVEN manager called twice
        manager.create();
        manager.create();

        // WHEN manager closed once
        manager.close();

        // THEN context is still active
        assertThat(manager.get()).isNotNull();
        assertThat(listener.closed).isEqualTo(0);

        // WHEN manager closed again
        manager.close();

        // THEN context is inactive
        assertThat(manager.get()).isNull();
        assertThat(listener.created).isEqualTo(1);
    }

    private RequestContext createNew() {
        manager.create();
        return manager.get();
    }

    /**
     * Increments a count when a {@link RequestContextCreatedEvent} fires
     */
    private static class RequestContextCreatedListener {

        private int created;
        private int closed;

        @Subscribe public void onRequestContextCreated(final RequestContextCreatedEvent event) {
            created++;
        }

        @Subscribe public void onRequestContextClosed(final RequestContextClosedEvent event) {
            closed++;
        }

        @Subscribe public void onDeadEvent(final DeadEvent event) {
            fail("unexpected dead event");
        }
    }
}
