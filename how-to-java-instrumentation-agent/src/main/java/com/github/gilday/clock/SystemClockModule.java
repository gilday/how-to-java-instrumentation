package com.github.gilday.clock;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import org.threeten.bp.Clock;

/**
 * Dagger module which provides a system {@link org.threeten.bp.Clock}
 */
@Module(library = true)
public class SystemClockModule {

    @Provides @Singleton Clock provideClock() { return Clock.systemUTC(); }
}
