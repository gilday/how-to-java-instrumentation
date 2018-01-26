package com.github.gilday.context;

import javax.inject.Singleton;

import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bus.EventBusModule;
import com.google.common.eventbus.EventBus;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the context package
 */
@Module(includes = EventBusModule.class, library = true)
public class ContextModule {

    @Provides @Singleton RequestContextManager provideContext(final EventBus bus) {
        return new ThreadLocalRequestContextManager(bus);
    }
}
