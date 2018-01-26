package com.github.gilday.bus;

import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Provides an {@link EventBus} singleton
 */
public class EventBusBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bindAsContract(EventBus.class).in(Singleton.class);
    }
}
