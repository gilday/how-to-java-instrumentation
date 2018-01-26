package com.github.gilday.stringcount;

import javax.inject.Singleton;

import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.bus.EventBusModule;
import com.github.gilday.clock.SystemClockModule;
import com.github.gilday.context.ContextModule;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger module for the string count package
 */
@Module(library = true, includes = {
    EventBusModule.class,
    ContextModule.class,
    SystemClockModule.class
})
public class StringCountModule {

    StringCountModule() {
        // for reasons I cannot explain, if the counter is not explicitly created before it is lazily created via the
        // CounterFactory when needed, then the thread hangs, so simply instantiate as a side-effect to prevent this
        // quirky behavior
        new SimpleCounter();
    }

    @Provides @Singleton public StringsAllocatedRecordStore provideStore() { return new StringsAllocatedRecordStore(); }

    @Provides @Singleton public CounterFactory provideCounterFactory() { return new SimpleCounter.Factory(); }

    @Provides @Singleton public Counter provideCounter(final RequestContextAwareCounter counter) { return counter; }
}
