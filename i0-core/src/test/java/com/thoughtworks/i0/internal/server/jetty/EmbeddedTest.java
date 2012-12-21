package com.thoughtworks.i0.internal.server.jetty;

import com.google.inject.servlet.ServletModule;
import com.thoughtworks.i0.config.HttpConfiguration;
import com.thoughtworks.i0.config.builder.ConfigurationBuilder;
import com.thoughtworks.i0.test.helpers.HttpClientHelper;
import org.junit.After;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.thoughtworks.i0.config.builder.ConfigurationBuilder.config;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmbeddedTest {


    private Embedded server;

    @After
    public void after() throws Exception {
        if (server != null) server.stop();
    }

    @Test
    public void should_configure_server_as_http_service() throws Exception {
        startServer(config().http().port(8080).build());
        assertThat(HttpClientHelper.get("http://localhost:8080/message"), is("message"));
    }

    @Test
    public void should_configure_server_as_https_service() throws Exception {
        startServer(config().http().port(8080)
                .ssl()
                .keyStore(getClass().getResource("test.keystore").getPath(), "password")
                .trustStore(getClass().getResource("test.keystore").getPath(), "password")
                .end().build());
        assertThat(HttpClientHelper.get("https://localhost:8080/message"), is("message"));
    }

    private void startServer(HttpConfiguration configuration) throws Exception {
        server = new Embedded(configuration);
        server.addServletContext("/", true, new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/message").with(new HttpServlet() {
                    @Override
                    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                        resp.getWriter().append("message");
                    }
                });
            }
        });
        server.start(false);
    }
}
