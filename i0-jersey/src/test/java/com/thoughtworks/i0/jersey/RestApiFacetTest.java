package com.thoughtworks.i0.jersey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.thoughtworks.i0.core.Launcher;
import com.thoughtworks.i0.core.ServletContainer;
import com.thoughtworks.i0.jersey.api.AutoScan;
import com.thoughtworks.i0.jersey.api.Specified;
import com.thoughtworks.i0.jersey.api.V2;
import com.thoughtworks.i0.jersey.api.p2.Data;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

import static com.sun.jersey.api.client.Client.create;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RestApiFacetTest {
    private ServletContainer server;

    @Test
    public void should_auto_scan_all_packages() throws Exception {
        server = Launcher.launch(new AutoScan(), false);
        assertThat(get("http://localhost:8051/autoscan/api/p1"), is("resource1"));
        assertThat(get("http://localhost:8051/autoscan/api/p2"), is("resource2"));
    }

    @Test
    public void should_register_all_packages_under_different_prefix() throws Exception {
        server = Launcher.launch(new V2(), false);
        assertThat(get("http://localhost:8051/autoscan/api/v2/p1"), is("resource1"));
        assertThat(get("http://localhost:8051/autoscan/api/v2/p2"), is("resource2"));
    }

    @Test(expected = UniformInterfaceException.class)
    public void should_auto_scan_specified_packages() throws Exception {
        server = Launcher.launch(new Specified(), false);
        assertThat(get("http://localhost:8051/autoscan/api/p2"), is("resource2"));
        get("http://localhost:8051/autoscan/api/p1");
    }

    @Test
    public void should_support_json_response() throws Exception {
        server = Launcher.launch(new Specified(), false);
        assertThat(json("http://localhost:8051/autoscan/api/p2/data", Data.class), is(new Data("data")));
    }

    @Test
    public void should_support_json_request() throws Exception {
        server = Launcher.launch(new Specified(), false);
        assertThat(create().resource("http://localhost:8051/autoscan/api/p2/echo").type(APPLICATION_JSON_TYPE).accept(APPLICATION_JSON_TYPE).post(Data.class, new Data("value")), is(new Data("value")));
    }

    private String get(String uri) {
        return create().resource(uri).get(String.class);
    }

    private <T> T json(String uri, Class<T> type) throws IOException {
        String json = create().resource(uri).accept(APPLICATION_JSON_TYPE).get(String.class);
        return new ObjectMapper().reader(type).readValue(json);
    }

    @After
    public void after() throws Exception {
        if (server != null) server.stop();
    }
}
