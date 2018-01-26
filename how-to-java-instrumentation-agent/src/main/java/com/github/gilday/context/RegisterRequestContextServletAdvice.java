package com.github.gilday.context;

import com.github.gilday.bootstrap.AgentServiceLocator;
import com.github.gilday.bootstrap.context.RequestContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice.OnMethodEnter;
import net.bytebuddy.asm.Advice.OnMethodExit;

/**
 * Servlet decorator which creates a new {@link RequestContext} before a request is serviced, and destroys that context
 * at the end of a request
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterRequestContextServletAdvice {

    @OnMethodEnter static void enter() {
        AgentServiceLocator.requestContextManager.create();
    }

    @OnMethodExit static void exit() {
        AgentServiceLocator.requestContextManager.close();
    }
}
