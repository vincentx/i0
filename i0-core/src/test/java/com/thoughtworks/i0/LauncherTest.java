package com.thoughtworks.i0;

import com.google.common.collect.ImmutableMap;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.builder.ConfigurationBuilder;
import com.thoughtworks.i0.server.JettyServer;
import com.thoughtworks.i0.server.sample.inject.services.MessageProvider;
import org.eclipse.jetty.client.HttpClient;
import org.junit.After;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LauncherTest {
    private static final Configuration config = new ConfigurationBuilder() {
        {
            http().port(8080);
        }
    }.build();

    @Test
    public void should_use_application_name_as_root_context() throws Exception {
        server = new Launcher(modules(new TestServletModule()), config).launch(false, "test");
        assertThat(get("http://localhost:8080/test/s1"), is("servlet1\n"));
    }

    @Test
    public void should_register_all_resources_as_api() throws Exception {
        server = new Launcher(modules(new TestResourceModule()), config).launch(false, "test");
        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("message"));
    }

    @Test
    public void should_auto_scan_servlets_and_api_from_base_package() throws Exception {
        server = new Launcher(modules(new TestRootModule()), config).launch(false, "test");

        assertThat(get("http://localhost:8080/test/s1"), is("servlet1\n"));
        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("message"));
    }

    @Test
    public void should_inject_service_to_resource_and_servlets() throws Exception {
        server = new Launcher(modules(new TestInjectModule()), config).launch(false, "test");

        assertThat(get("http://localhost:8080/test/s1"), is("injected"));
        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("injected"));
    }

    @Test
    public void should_override_inject_service_to_resource_and_servlets() throws Exception {
        server = new Launcher(modules(new TestInjectOverrideModule()), config).launch(false, "test");

        assertThat(get("http://localhost:8080/test/s1"), is("injection override"));
        assertThat(get("http://localhost:8080/test/api/resource1/message"), is("injection override"));
    }

    @Test
    public void should_auto_scan_application_modules() throws Exception {
        server = new Launcher(config).launch(false, "auto-scan");
        assertThat(get("http://localhost:8080/auto-scan/s1"), is("auto-scan"));
        assertThat(get("http://localhost:8080/auto-scan/api/resource1/message"), is("auto-scan"));
    }

    private Map<String, ApplicationModule> modules(ApplicationModule... modules) throws Exception {
        ImmutableMap.Builder<String, ApplicationModule> builder = new ImmutableMap.Builder<>();
        for (ApplicationModule module : modules)
            builder.put(module.getApplication().name(), module);
        return builder.build();
    }

    private HttpClient client;
    private JettyServer server;

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

    @Application(name = "test",
            servlets = "com.thoughtworks.i0.server.sample.simple.servlets")
    static class TestServletModule extends ApplicationModule {
    }

    @Application(name = "test",
            api = "com.thoughtworks.i0.server.sample.simple.api")
    static class TestResourceModule extends ApplicationModule {
    }

    @Application(name = "test",
            root = "com.thoughtworks.i0.server.sample.simple")
    static class TestRootModule extends ApplicationModule {
    }

    @Application(name = "test",
            root = "com.thoughtworks.i0.server.sample.inject")
    static class TestInjectModule extends ApplicationModule {
    }

    static class TestInjectOverrideModule extends TestInjectModule {
        @Override
        protected void configService() {
            bind(MessageProvider.class).toInstance(new MessageProvider() {
                @Override
                public String message() {
                    return "injection override";
                }
            });
        }
    }
}
