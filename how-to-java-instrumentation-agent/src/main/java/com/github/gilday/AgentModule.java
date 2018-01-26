package com.github.gilday;

import com.github.gilday.context.ContextModule;
import com.github.gilday.stringcount.StringCountModule;
import dagger.Module;

@Module(injects = Agent.class, includes = {
    ContextModule.class,
    StringCountModule.class
})
class AgentModule { }
