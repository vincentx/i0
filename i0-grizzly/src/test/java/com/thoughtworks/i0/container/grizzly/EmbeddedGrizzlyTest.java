package com.thoughtworks.i0.container.grizzly;

import com.thoughtworks.i0.container.grizzly.internal.EmbeddedContainer;
import com.thoughtworks.i0.core.Launcher;
import com.thoughtworks.i0.core.ServletContainer;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.grizzly.http.server.util.MimeType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EmbeddedGrizzlyTest {
    private ServletContainer server;

    @Before
    public void before() throws Exception {
        server = Launcher.launch(new EmbeddedContainer(), false);
    }

    @After
    public void after() throws Exception {
        if (server != null) server.stop();
    }

    @Test
    public void should_launch_embedded_grizzly() throws Exception {
        assertThat(getAsString("http://localhost:8051/embedded/1/1"), is("servlet1"));
    }

    @Test
    public void should_load_html_asset_from_resource_path() throws Exception {
        assertThat(getAsString("http://localhost:8051/embedded/static/test.html"), is("<h1>static</h1>"));
    }

    @Test
    public void should_inject_service_to_servlet() throws Exception {
        assertThat(getAsString("http://localhost:8051/embedded/2/1"), is("injected"));
    }

    @Test
    public void should_load_js_asset_from_resource_path() throws Exception {
        ContentResponse response = get("http://localhost:8051/embedded/static/javascripts/jquery.min.js");
        assertThat(response.getStatus(), is(200));
        assertThat(getMimeType(response), is("text/javascript"));
    }

    @Test
    public void should_load_gif_asset_from_resource_path() throws Exception {
        ContentResponse response = get("http://localhost:8051/embedded/static/images/spinner.gif");
        assertThat(response.getStatus(), is(200));
        assertThat(getMimeType(response), is("image/gif"));
    }

    @Test
    public void should_load_css_asset_from_resource_path() throws Exception {
        ContentResponse response = get("http://localhost:8051/embedded/static/styles/global.css");
        assertThat(response.getStatus(), is(200));
        assertThat(getMimeType(response), is("text/css"));
    }

    @Test
    public void should_load_extension_font_eot_asset_from_resource_path() throws Exception {
        ContentResponse response = get("http://localhost:8051/embedded/static/fonts/iconFont.eot");
        assertThat(response.getStatus(), is(200));
        assertThat(getMimeType(response), is("application/vnd.ms-fontobject"));
    }

    @Test
    public void should_load_extension_font_svg_asset_from_resource_path() throws Exception {
        ContentResponse response = get("http://localhost:8051/embedded/static/fonts/iconFont.svg");
        assertThat(response.getStatus(), is(200));
        assertThat(getMimeType(response), is("image/svg+xml"));
    }

    private String getMimeType(ContentResponse response) {
        return response.getHeaders().getStringField("Content-Type");
    }

    public static String getAsString(String url) throws Exception {
        return new String(get(url).getContent());
    }

    private static ContentResponse get(String url) throws Exception {
        HttpClient client = new HttpClient(new SslContextFactory());
        client.start();
        ContentResponse response;
        try {
            response = client.GET(url).get();
        } finally {
            client.stop();
        }
        return response;
    }
}
