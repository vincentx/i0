package com.thoughtworks.i0.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.EnumSet;

import static javax.servlet.DispatcherType.REQUEST;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class JettyServer {
    private final Server server;

    public JettyServer(String name, int port, Module... modules) {
        this.server = new Server(port);
        configure(name, modules);
    }

    private void configure(String name, final Module[] modules) {
        ServletContextHandler handler = new ServletContextHandler(server, root(name), NO_SESSIONS);
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.of(REQUEST));
        handler.addServlet(DefaultServlet.class, "/*");

        handler.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return Guice.createInjector(modules);
            }
        });
    }

    private String root(String name) {
        return name.startsWith("/") ? name : "/" + name;
    }

    public void start(boolean standalone) throws Exception {
        server.start();
        if (standalone) server.join();
    }

    public void close() throws Exception {
        server.stop();
    }
}
