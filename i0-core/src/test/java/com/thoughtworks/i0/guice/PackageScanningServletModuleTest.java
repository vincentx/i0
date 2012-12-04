package com.thoughtworks.i0.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.After;
import org.junit.Test;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PackageScanningServletModuleTest {
    private Server server;
    private HttpClient client;

    @Test
    public void should_register_all_servlets_from_specific_package_with_url_patterns() throws Exception {
        start(new PackageScanningServletModule("com.thoughtworks.i0.guice.servlets.urlpatterns"));
        assertThat(get("http://localhost:8080/s1"), is("servlet1\n"));
        assertThat(get("http://localhost:8080/s2"), is("servlet1\n"));
        assertThat(get("http://localhost:8080/a.jpg"), is("servlet2\n"));
        assertThat(get("http://localhost:8080/b.jpg"), is("servlet2\n"));
    }

    @Test
    public void should_register_all_servlet_from_specific_package_with_values() throws Exception {
        start(new PackageScanningServletModule("com.thoughtworks.i0.guice.servlets.values"));
        assertThat(get("http://localhost:8080/s1"), is("servlet1\n"));
        assertThat(get("http://localhost:8080/s2"), is("servlet1\n"));
        assertThat(get("http://localhost:8080/a.jpg"), is("servlet2\n"));
        assertThat(get("http://localhost:8080/b.jpg"), is("servlet2\n"));
    }

    @Test
    public void should_register_all_filter_from_specific_package_with_url_patterns() throws Exception {
        start(new PackageScanningServletModule("com.thoughtworks.i0.guice.filters.urlpatterns", "com.thoughtworks.i0.guice.servlets.urlpatterns"));
        assertThat(get("http://localhost:8080/s1"), is("filter1\nservlet1\n"));
        assertThat(get("http://localhost:8080/a.jpg"), is("filter2\nservlet2\n"));
    }

    @Test
    public void should_register_all_filter_from_specific_package_with_values() throws Exception {
        start(new PackageScanningServletModule("com.thoughtworks.i0.guice.filters.values", "com.thoughtworks.i0.guice.servlets.values"));
        assertThat(get("http://localhost:8080/s1"), is("filter1\nservlet1\n"));
        assertThat(get("http://localhost:8080/a.jpg"), is("filter2\nservlet2\n"));
    }

    private void start(final Module... modules) throws Exception {
        server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(server, "/", NO_SESSIONS);
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        handler.addServlet(DefaultServlet.class, "/*");

        handler.addEventListener(new GuiceServletContextListener() {
            @Override
            protected Injector getInjector() {
                return Guice.createInjector(modules);
            }
        });
        server.start();
    }

    private String get(String url) throws Exception {
        return new String(client().GET(url).get().content());
    }

    private HttpClient client() throws Exception {
        if (client == null || !client.isRunning()) {
            client = new HttpClient();
            client.start();
        }
        return client;
    }

    @After
    public void after() throws Exception {
        if (server != null && server.isRunning()) server.stop();
        if (client != null && client.isRunning()) client.stop();
    }
}
