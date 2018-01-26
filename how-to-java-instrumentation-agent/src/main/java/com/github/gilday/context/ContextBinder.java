package com.github.gilday.context;

import javax.inject.Singleton;

import com.github.gilday.bootstrap.context.RequestContextManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Provides the {@link RequestContextManager} singleton
 */
public class ContextBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(ThreadLocalRequestContextManager.class).to(RequestContextManager.class).in(Singleton.class);
    }
}
