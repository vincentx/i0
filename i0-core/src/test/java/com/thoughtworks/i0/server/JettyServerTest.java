package com.thoughtworks.i0.server;

import com.google.inject.AbstractModule;
import com.thoughtworks.i0.Application;
import com.thoughtworks.i0.server.sample.inject.services.MessageProvider;
import org.eclipse.jetty.client.HttpClient;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JettyServerTest {
    private HttpClient client;
    private JettyServer server;

    @Test
    public void should_use_application_name_as_root_context() throws Exception {
        server = new JettyServer(new Application("test") {{
            port(8080);

            servlets("com.thoughtworks.i0.server.sample.simple.servlets;");
        }});

        server.start(false);
        assertThat(get("http://localhost:8080/test/s1"), is("servlet1\n"));
    }

    @Test
    public void should_register_all_resources_as_api() throws Exception {
        server = new JettyServer(new Application("test") {{
            port(8080);

            api("com.thoughtworks.i0.server.sample.simple.api;");
        }});

        server.start(false);
        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("message"));
    }

    @Test
    public void should_auto_scan_servlets_and_api_from_base_package() throws Exception {
        server = new JettyServer(new Application("test") {{
            port(8080);

            root("com.thoughtworks.i0.server.sample.simple");
        }});

        server.start(false);
        assertThat(get("http://localhost:8080/test/s1"), is("servlet1\n"));
        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("message"));
    }

    @Test
    public void should_inject_service_to_resource_and_servlets() throws Exception {
        server = new JettyServer(new Application("test") {{
            port(8080);

            root("com.thoughtworks.i0.server.sample.inject");

            install(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(MessageProvider.class).toInstance(new MessageProvider() {
                        @Override
                        public String message() {
                            return "injected";
                        }
                    });
                }
            });
        }});
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
