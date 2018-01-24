package com.github.gilday.stringcount;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.stringcount.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RequestContextAwareCounter}
 */
class RequestContextAwareCounterTest {

    private RequestContextAwareCounter counter;
    private RequestContextManager ctxManager;

    @BeforeEach
    void before() {
        ctxManager = mock(RequestContextManager.class);
    }

    @Test
    void it_increments_if_request_context_available() {
        // GIVEN the context manager returns an existing context
        final RequestContext ctx = mock(RequestContext.class);
        when(ctxManager.get()).thenReturn(ctx);
        final Counter inner = new ThreadUnsafeCounter();
        counter = new RequestContextAwareCounter(() -> inner, ctxManager);
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
        counter = new RequestContextAwareCounter(() -> inner, ctxManager);
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
        counter = new RequestContextAwareCounter(() -> inner, ctxManager);
        when(ctxManager.get()).thenReturn(null);

        // EXPECT retrieve to throw
        assertThatThrownBy(() -> counter.get()).isInstanceOf(IllegalStateException.class);
    }

}
