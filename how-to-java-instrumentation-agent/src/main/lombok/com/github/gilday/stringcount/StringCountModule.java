package com.github.gilday.stringcount;

import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.stringcount.jmx.StringsAllocatedGauge;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the string count package
 */
@Module
public class StringCountModule {

    @Provides public static CounterFactory provideCounterFactory() { return new SimpleCounter.Factory(); }

    @Provides public static Counter provideCounter(final RequestContextAwareCounter counter) { return counter; }

    @Provides public static StringsAllocatedGauge provideGauge(final StringsAllocatedGauge gauge) { return gauge; }
}
