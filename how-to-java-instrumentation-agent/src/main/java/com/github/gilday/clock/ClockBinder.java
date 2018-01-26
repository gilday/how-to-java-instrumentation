package com.github.gilday.clock;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.threeten.bp.Clock;

/**
 * Provides the System {@link Clock} singleton
 */
public class ClockBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(Clock.systemUTC()).to(Clock.class);
    }
}
