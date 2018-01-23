package com.github.gilday.context;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.gilday.bootstrap.context.RequestContext;
import com.github.gilday.bootstrap.context.Symbol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link RequestContextImpl}
 */
class RequestContextImplTest {

    private RequestContext ctx;

    @BeforeEach
    void before() {
        ctx = new RequestContextImpl();
    }

    @Test
    void it_considers_two_symbol_instances_with_identical_names_to_be_distinct() {
        // GIVEN two distinct keys, albeit with identical names and types
        final Symbol<String> key1 = Symbol.of("key");
        final Symbol<String> key2 = Symbol.of("key");

        // WHEN store values with two different keys
        ctx.put(key1, "foo");
        ctx.put(key2, "bar");

        // THEN stores distinct records
        assertThat(ctx.get(key1)).isEqualTo("foo");
        assertThat(ctx.get(key2)).isEqualTo("bar");
    }
}
