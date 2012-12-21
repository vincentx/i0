package com.thoughtworks.i0.projects.aplication.module.servlets;

import javax.inject.Singleton;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Singleton
@WebFilter(urlPatterns = "/web/b/1", value = "/web/b/2")
public class BFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.getWriter().append("filtered: ");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
