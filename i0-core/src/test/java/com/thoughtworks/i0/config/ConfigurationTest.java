package com.thoughtworks.i0.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.thoughtworks.i0.config.builder.ConfigurationBuilder;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {
    @Test
    public void should_read_config_from_yaml() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Configuration configuration = mapper.readValue(
                "database:\n" +
                        "  driver: driver\n" +
                        "  url: url\n" +
                        "  user: user\n" +
                        "  password: password\n" +
                        "http:\n" +
                        "  port: 8051\n", Configuration.class);

        assertThat(configuration, is(new ConfigurationBuilder() {
            {
                database().driver("driver").url("url").user("user").password("password");
                http().port(8051);
            }
        }.build()));
    }

    @Test
    public void should_give_default_value_to_config() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Configuration configuration = mapper.readValue(
                "database:\n" +
                        "  driver: driver\n" +
                        "  url: url\n", Configuration.class);
        assertThat(configuration, is(new ConfigurationBuilder() {
            {
                database().driver("driver").url("url").user("").password("");
                http().port(8080);
            }
        }.build()));
    }
}
