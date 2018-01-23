package com.github.gilday.context;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.github.gilday.bootstrap.context.RequestContext;
import lombok.RequiredArgsConstructor;

/**
 * Servlet decorator which uses {@link RegisterRequestContextServletAdvice} to create a new {@link RequestContext}
 * before a request is serviced, and destroys that context at the end of a request
 */
@RequiredArgsConstructor(staticName = "wrap")
public class RegisterRequestContextServletDecorator implements Servlet {

    private final Servlet inner;

    @Override
    public void init(final ServletConfig config) throws ServletException { inner.init(config); }

    @Override
    public ServletConfig getServletConfig() { return inner.getServletConfig(); }

    @Override
    public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
        RegisterRequestContextServletAdvice.enter();
        inner.service(req, res);
        RegisterRequestContextServletAdvice.exit();
    }

    @Override
    public String getServletInfo() { return inner.getServletInfo(); }

    @Override
    public void destroy() { inner.destroy(); }
}
