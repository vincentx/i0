package com.thoughtworks.i0;

import com.thoughtworks.i0.config.builder.HttpConfigurationBuilder;
import com.thoughtworks.i0.internal.server.jetty.Embedded;
import com.thoughtworks.i0.projects.aplication.module.ApplicationModuleTestApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.i0.test.helpers.HttpClientHelper.get;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplicationModuleTest {
    private Embedded server;

    @Before
    public void before() throws Exception {
        server = new Embedded(new HttpConfigurationBuilder().build());
        ApplicationModuleTestApplication module = new ApplicationModuleTestApplication();
        server.addServletContext(module.name(), true, module);
        server.start(false);
    }

    @After
    public void after() throws Exception {
        if (server.isRunning()) server.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_illegal_state_exception_if_application_module_does_not_have_an_application_annotation() {
        new ApplicationModuleWithoutAnnotation();
    }

    @Test
    public void should_auto_scan_servlets_filters_and_resources() throws Exception {
        assertThat(get("http://localhost:8080/application/api/root"), is("/api/root"));
    }

    @Test
    public void should_auto_scan_servlets_and_map_to_url_by_patterns_and_values() throws Exception {
        assertThat(get("http://localhost:8080/application/web/a/1"), is("/web/a"));
        assertThat(get("http://localhost:8080/application/web/a/2"), is("/web/a"));
        assertThat(get("http://localhost:8080/application/web/a/3"), is("/web/a"));
        assertThat(get("http://localhost:8080/application/web/a/4"), is("/web/a"));
    }

    @Test
    public void should_auto_scan_filters_and_map_to_url_by_patterns_and_values() throws Exception {
        assertThat(get("http://localhost:8080/application/web/b/1"), is("filtered: /web/b"));
        assertThat(get("http://localhost:8080/application/web/b/2"), is("filtered: /web/b"));
    }

    @Test
    public void should_auto_scan_module_and_install_to_module() throws Exception {
        assertThat(get("http://localhost:8080/application/web/c/1"), is("/web/c"));
        assertThat(get("http://localhost:8080/application/web/c/2"), is("/web/c"));
    }

    static class ApplicationModuleWithoutAnnotation extends ApplicationModule {

    }
}
