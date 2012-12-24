package com.thoughtworks.i0.jetty;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.LoggingConfiguration;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.core.Launcher;
import com.thoughtworks.i0.core.ServletContainer;
import com.thoughtworks.i0.core.internal.logging.Logging;
import com.thoughtworks.i0.jetty.servlet.EmbeddedContainer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmbeddedJettyTest {
    private ServletContainer server;

    @After
    public void after() throws Exception {
        if (server != null) server.stop();
    }

    @Test
    public void should_launch_embedded_jetty() throws Exception {
        server = Launcher.launch(new EmbeddedContainer(), false);
        assertThat(get("http://localhost:8051/embedded/1/1"), is("servlet1"));
    }

    @Test
    public void should_inject_service_to_servlet() throws Exception {
        server = Launcher.launch(new EmbeddedContainer(), false);
        assertThat(get("http://localhost:8051/embedded/2/1"), is("injected"));
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
}
