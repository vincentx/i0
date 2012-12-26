package com.thoughtworks.i0.jersey;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.thoughtworks.i0.core.Launcher;
import com.thoughtworks.i0.core.ServletContainer;
import com.thoughtworks.i0.jersey.api.AutoScan;
import com.thoughtworks.i0.jersey.api.Specified;
import com.thoughtworks.i0.jersey.api.V2;
import com.thoughtworks.i0.jersey.api.p2.Data;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.sun.jersey.api.client.Client.create;
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
    public void should() {
        System.out.println(get("http://forge.puppetlabs.com/users/puppetlabs/modules/java/releases/find.json"));
    }

    private String get(String uri) {
        return create().resource(uri).get(String.class);
    }

    private <T> T json(String uri, Class<T> type) {
        return create().resource(uri).accept(MediaType.APPLICATION_JSON_TYPE).get(type);
    }

    @After
    public void after() throws Exception {
        if (server != null) server.stop();
    }
}
