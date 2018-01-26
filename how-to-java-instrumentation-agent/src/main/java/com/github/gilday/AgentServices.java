package com.github.gilday;

import com.github.gilday.bootstrap.context.RequestContextManager;
import com.github.gilday.bootstrap.stringcount.Counter;
import com.github.gilday.bus.EventBusModule;
import com.github.gilday.clock.SystemClockModule;
import com.github.gilday.context.ContextModule;
import com.github.gilday.stringcount.StringCountModule;
import dagger.Component;

/**
 * Agent singletons needed by instrumented classes
 */
@Component(modules = {
    SystemClockModule.class,
    EventBusModule.class,
    ContextModule.class,
    StringCountModule.class
})
public interface AgentServices {
    RequestContextManager contextManager();
    Counter counter();
}
