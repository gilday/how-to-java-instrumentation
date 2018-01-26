package com.github.gilday.bus;

import com.google.common.eventbus.EventBus;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the agent's {@link EventBus}
 */
@Module
public class EventBusModule {

    @Provides public static EventBus bus() {
        return new EventBus();
    }
}
