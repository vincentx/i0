package com.thoughtworks.i0.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.thoughtworks.i0.Application;
import com.thoughtworks.i0.guice.PackageScanningServletModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.util.EnumSet;
import java.util.Properties;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.toArray;
import static javax.servlet.DispatcherType.REQUEST;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

public class JettyServer {
    private final Server server;

    public JettyServer(Application application) {
        this.server = new Server(application.port());
        configure(application);
    }

    private void configure(final Application application) {
        final ImmutableSet.Builder<Module> modules = new ImmutableSet.Builder<>();

        modules.addAll(application.modules());

        scanServlets(application, modules);
        scanApi(application, modules);
        persist(application, modules);

        ServletContextHandler handler = new ServletContextHandler(server, rootPath(application), NO_SESSIONS);
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.of(REQUEST));
        handler.addServlet(DefaultServlet.class, "/*");

        handler.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return Guice.createInjector(modules.build());
            }
        });
    }

    private void persist(Application application, ImmutableSet.Builder<Module> modules) {
        if (application.jdbcUrl() == null || application.jdbcDriver() == null) return;
        Properties jpaProperties = new Properties();
        jpaProperties.put("javax.persistence.jdbc.url", application.jdbcUrl());
        jpaProperties.put("javax.persistence.jdbc.driver", application.jdbcDriver());
        for (String persistenceUnit : application.persistence())
            modules.add(new JpaPersistModule(persistenceUnit).properties(jpaProperties));
        modules.add(new ServletModule() {
            @Override
            protected void configureServlets() {
                filter("/*").through(PersistFilter.class);
            }
        });
    }

    private void scanApi(final Application application, ImmutableSet.Builder<Module> modules) {
        if (application.api().size() == 0) return;
        modules.add(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/api/*").with(GuiceContainer.class, new ImmutableMap.Builder<String, String>()
                        .put(PackagesResourceConfig.PROPERTY_PACKAGES, on("; ").skipNulls().join(application.api())).build());
            }
        });
    }

    private String rootPath(Application application) {
        return application.name().startsWith("/") ? application.name() : "/" + application.name();
    }

    private void scanServlets(Application application, ImmutableSet.Builder<Module> modules) {
        if (application.servlets().size() == 0) return;
        modules.add(new PackageScanningServletModule(toArray(application.servlets(), String.class)));
    }

    public void start(boolean standalone) throws Exception {
        server.start();
        if (standalone) server.join();
    }

    public void close() throws Exception {
        server.stop();
    }
}
