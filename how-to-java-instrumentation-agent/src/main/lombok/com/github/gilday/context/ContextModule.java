package com.github.gilday.context;

import com.github.gilday.bootstrap.context.RequestContextManager;
import com.google.common.eventbus.EventBus;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the context package
 */
@Module
public class ContextModule {

    @Provides static RequestContextManager context(final EventBus bus) {
        return new ThreadLocalRequestContextManager(bus);
    }
}
