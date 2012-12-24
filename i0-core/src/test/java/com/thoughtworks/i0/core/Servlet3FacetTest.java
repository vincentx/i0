package com.thoughtworks.i0.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.core.internal.logging.Logging;
import com.thoughtworks.i0.core.servlet3.AutoScan;
import com.thoughtworks.i0.core.servlet3.Specified;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.EnumSet;

import static javax.servlet.DispatcherType.REQUEST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Servlet3FacetTest {
    private Server server;

    @Test
    public void should_auto_scan_all_packages() throws Exception {
        startServer(new AutoScan());
        assertThat(get("http://localhost:8051/1/1"), is("servlet1"));
        assertThat(get("http://localhost:8051/1/2"), is("filtered:servlet1"));
        assertThat(get("http://localhost:8051/1/3"), is("filtered:servlet1"));
        assertThat(get("http://localhost:8051/2/1"), is("servlet2"));
        assertThat(get("http://localhost:8051/2/2"), is("filtered:servlet2"));
        assertThat(get("http://localhost:8051/2/3"), is("filtered:servlet2"));
    }

    @Test
    public void should_auto_scan_specified_packages() throws Exception {
        startServer(new Specified());
        assertThat(get("http://localhost:8051/2/1"), is("servlet2"));
        assertThat(get("http://localhost:8051/2/2"), is("filtered:servlet2"));
        assertThat(get("http://localhost:8051/2/3"), is("filtered:servlet2"));
        assertThat(status("http://localhost:8051/1/1"), is(404));
    }

    @After
    public void after() throws Exception {
        if (server != null) server.stop();
    }

    private void startServer(final Module module) throws Exception {
        server = new Server(8051);
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.of(REQUEST));
        handler.addServlet(DefaultServlet.class, "/*");

        handler.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return Guice.createInjector(module);
            }
        });

        server.setHandler(handler);
        server.start();
    }

    public static String get(String url) throws Exception {
        HttpClient client = new HttpClient(new SslContextFactory());
        client.start();
        try {
            return new String(client.GET(url).get().getContent());
        } finally {
            client.stop();
        }
    }

    public static int status(String url) throws Exception {
        HttpClient client = new HttpClient(new SslContextFactory());
        client.start();
        try {
            return client.GET(url).get().getStatus();
        } finally {
            client.stop();
        }
    }

}
