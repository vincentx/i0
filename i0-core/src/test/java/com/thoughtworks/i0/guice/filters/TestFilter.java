package com.thoughtworks.i0.guice.filters;

import javax.servlet.*;
import java.io.IOException;

public class TestFilter implements Filter {
    private final String message;

    public TestFilter(String message) {
        this.message = message;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.getWriter().append(message);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
