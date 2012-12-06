package com.thoughtworks.i0;

import com.thoughtworks.i0.Launcher;
import com.thoughtworks.i0.config.builder.ApplicationConfig;
import com.thoughtworks.i0.server.JettyServer;
import org.eclipse.jetty.client.HttpClient;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LauncherTest {
    private HttpClient client;
    private JettyServer server;

    @Test
    public void should_use_application_name_as_root_context() throws Exception {
        server = new Launcher("test", new ApplicationConfig() {
            {
                config().servlets("com.thoughtworks.i0.server.sample.simple.servlets");
            }
        }.configuration(), null, 8080).launch(false);

        assertThat(get("http://localhost:8080/test/s1"), is("servlet1\n"));
    }

    @Test
    public void should_register_all_resources_as_api() throws Exception {
        server = new Launcher("test", new ApplicationConfig() {
            {
                config().api("com.thoughtworks.i0.server.sample.simple.api");
            }
        }.configuration(), null, 8080).launch(false);

        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("message"));
    }

    @Test
    public void should_auto_scan_servlets_and_api_from_base_package() throws Exception {
        server = new Launcher("test", new ApplicationConfig() {
            {
                config().root("com.thoughtworks.i0.server.sample.simple");
            }
        }.configuration(), null, 8080).launch(false);

        assertThat(get("http://localhost:8080/test/s1"), is("servlet1\n"));
        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("message"));
    }

    @Test
    public void should_inject_service_to_resource_and_servlets() throws Exception {
        server = new Launcher("test", new ApplicationConfig() {
            {
                config().root("com.thoughtworks.i0.server.sample.inject")
                        .services("com.thoughtworks.i0.server.sample.inject.services");
            }
        }.configuration(), null, 8080).launch(false);

        server.start(false);
        assertThat(get("http://localhost:8080/test/s1"), is("injected"));
        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("injected"));
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
        if (server != null) server.close();
        if (client != null && client.isRunning()) client.stop();
    }
}
