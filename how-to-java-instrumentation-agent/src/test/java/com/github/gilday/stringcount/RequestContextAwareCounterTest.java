package com.github.gilday.stringcount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;

import com.github.gilday.FakeClock;
import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.context.RequestContextClosedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RequestContextAwareCounter}
 */
class RequestContextAwareCounterTest {

    private Clock clock;
    private StringsAllocatedRecordStore store;
    private RequestContextAwareCounter counter;
    private RequestContextManager ctxManager;

    @BeforeEach
    void before() {
        clock = new FakeClock();
        ctxManager = mock(RequestContextManager.class);
        store = mock(StringsAllocatedRecordStore.class);
    }

    @Test
    void it_increments_if_request_context_available() {
        // GIVEN the context manager returns an existing context
        final RequestContext ctx = mock(RequestContext.class);
        when(ctxManager.get()).thenReturn(ctx);
        final Counter inner = new SimpleCounter();
        counter = new RequestContextAwareCounter(() -> inner, ctxManager, clock, store);
        when(ctx.get(counter.key)).thenReturn(inner);

        // WHEN increment twice then get
        counter.inc();
        counter.inc();
        final long value = counter.get();

        // THEN counter is incremented to 2
        assertThat(value).isEqualTo(2);
    }

    @Test
    void it_does_not_increment_if_no_request_context_available() {
        // GIVEN the context manager returns null context
        final Counter inner = mock(Counter.class);
        counter = new RequestContextAwareCounter(() -> inner, ctxManager, clock, store);
        when(ctxManager.get()).thenReturn(null);

        // WHEN increment
        counter.inc();

        // THEN does not delegate to inner
        verify(inner, never()).inc();
    }

    @Test
    void it_fails_when_retrieve_counter_value_outside_of_a_context() {
        // GIVEN the context manager returns null context
        final Counter inner = mock(Counter.class);
        counter = new RequestContextAwareCounter(() -> inner, ctxManager, clock, store);
        when(ctxManager.get()).thenReturn(null);

        // EXPECT retrieve to throw
        assertThatThrownBy(() -> counter.get()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void it_stores_new_strings_allocated_record_when_context_closes() {
        // GIVEN a counter with 100L string allocations counted in the current context
        final Counter inner = mock(Counter.class);
        when(inner.get()).thenReturn(100L);
        counter = new RequestContextAwareCounter(() -> inner, ctxManager, clock, store);
        final RequestContext ctx = mock(RequestContext.class);
        when(ctx.get(counter.key)).thenReturn(inner);
        when(ctxManager.get()).thenReturn(ctx);
        final RequestContextClosedEvent event = RequestContextClosedEvent.of(ctx);

        // WHEN call event handler
        counter.onRequestContextClosed(event);

        // THEN stores the count with request metadata
        verify(store).add(StringsAllocated.of(clock.instant(), 100));
    }
}
