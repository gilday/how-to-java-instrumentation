package com.github.gilday.bus;

import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the agent's {@link EventBus}
 */
@Module(library = true)
public class EventBusModule {

    @Provides @Singleton public EventBus provideBus() {
        return new EventBus();
    }
}
