package com.thoughtworks.i0.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.thoughtworks.i0.config.builder.ApplicationConfig;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ApplicationConfigurationTest {
    @Test
    public void should_read_config_from_yaml() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        ApplicationConfiguration configuration = mapper.readValue(
                "root: com.thoughtworks.i0.samples.app\n" +
                        "persist-unit: domain\n" +
                        "api: \n" +
                        "  - com.thoughtworks.i0.samples.app.api1\n" +
                        "  - com.thoughtworks.i0.samples.app.api2 \n" +
                        "servlets:\n" +
                        "  - com.thoughtworks.i0.samples.app.servlet1\n" +
                        "  - com.thoughtworks.i0.samples.app.servlet2 \n" +
                        "filters:\n" +
                        "  - com.thoughtworks.i0.samples.app.filter1\n" +
                        "  - com.thoughtworks.i0.samples.app.filter2 \n" +
                        "service-modules:\n" +
                        "  - com.thoughtworks.i0.samples.app.service1\n" +
                        "  - com.thoughtworks.i0.samples.app.service2\n" +
                        "env:\n" +
                        "  development:\n" +
                        "    api:\n" +
                        "      - com.thoughtworks.i0.samples.app.dev.api\n" +
                        "    servlets:\n" +
                        "      - com.thoughtworks.i0.samples.app.dev.servlet\n" +
                        "    filters:\n" +
                        "      - com.thoughtworks.i0.samples.app.dev.filter\n" +
                        "    service-modules:\n" +
                        "      - com.thoughtworks.i0.samples.app.dev.service\n" +
                        "    database:\n" +
                        "      driver: driver\n" +
                        "      url: url\n" +
                        "      user: user\n" +
                        "      password: password\n", ApplicationConfiguration.class);

        assertThat(configuration, is(notNullValue()));

        ApplicationConfig applicationConfig = new ApplicationConfig() {
            {
                config().root("com.thoughtworks.i0.samples.app")
                        .unit("domain")
                        .api("com.thoughtworks.i0.samples.app.api1", "com.thoughtworks.i0.samples.app.api2")
                        .servlets("com.thoughtworks.i0.samples.app.servlet1", "com.thoughtworks.i0.samples.app.servlet2")
                        .services("com.thoughtworks.i0.samples.app.service1", "com.thoughtworks.i0.samples.app.service2")
                        .filters("com.thoughtworks.i0.samples.app.filter1", "com.thoughtworks.i0.samples.app.filter2");
                config().environment("development")
                        .api("com.thoughtworks.i0.samples.app.dev.api")
                        .servlets("com.thoughtworks.i0.samples.app.dev.servlet")
                        .filters("com.thoughtworks.i0.samples.app.dev.filter")
                        .services("com.thoughtworks.i0.samples.app.dev.service")
                        .database()
                        .driver("driver")
                        .url("url")
                        .user("user")
                        .password("password");
            }
        };
        assertThat(configuration, is(applicationConfig.configuration()));
    }

    @Test
    public void should_read_config_from_yaml_without_db_definition() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        ApplicationConfiguration configuration = mapper.readValue(
                "root: com.thoughtworks.i0.samples.app\n" +
                        "persist-unit: domain\n" +
                        "api: \n" +
                        "  - com.thoughtworks.i0.samples.app.api1\n" +
                        "  - com.thoughtworks.i0.samples.app.api2 \n" +
                        "servlets:\n" +
                        "  - com.thoughtworks.i0.samples.app.servlet1\n" +
                        "  - com.thoughtworks.i0.samples.app.servlet2 \n" +
                        "filters:\n" +
                        "  - com.thoughtworks.i0.samples.app.filter1\n" +
                        "  - com.thoughtworks.i0.samples.app.filter2 \n" +
                        "env:\n" +
                        "  development:\n" +
                        "    api:\n" +
                        "      - com.thoughtworks.i0.samples.app.dev.api\n" +
                        "    servlets:\n" +
                        "      - com.thoughtworks.i0.samples.app.dev.servlet\n" +
                        "    filters:\n" +
                        "      - com.thoughtworks.i0.samples.app.dev.filter\n"
                , ApplicationConfiguration.class);
        assertThat(configuration.getEnvironments().get("development").getDatabaseConfiguration(), is(nullValue()));
    }
}
