package com.github.gilday;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.bus.EventBusBinder;
import com.github.gilday.clock.ClockBinder;
import com.github.gilday.context.ContextBinder;
import com.github.gilday.stringcount.StringCountBinder;
import com.github.gilday.stringcount.jmx.StringsAllocatedGauge;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.Test;

/**
 * Tests HK2 configuration
 */
class InitializeTest {

    @Test
    void provides_singletons() {
        final ServiceLocator locator = ServiceLocatorUtilities.bind(
            "how-to-java-instrumentation",
            new EventBusBinder(),
            new ClockBinder(),
            new ContextBinder(),
            new StringCountBinder()
        );

        // expose singletons to ServiceLocator
        for (final Class<?> clazz : Arrays.asList(RequestContextManager.class, Counter.class, StringsAllocatedGauge.class)) {
            assertThat(locator.getService(clazz))
                .withFailMessage("Expected service locator to create a %s", clazz)
                .isNotNull();
        }
    }
}
