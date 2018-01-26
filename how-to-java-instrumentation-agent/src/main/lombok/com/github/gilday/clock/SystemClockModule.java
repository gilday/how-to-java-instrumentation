package com.github.gilday.clock;

import dagger.Module;
import dagger.Provides;
import org.threeten.bp.Clock;

/**
 * Dagger module which provides a system {@link org.threeten.bp.Clock}
 */
@Module
public class SystemClockModule {

    @Provides static Clock clock() { return Clock.systemUTC(); }
}
