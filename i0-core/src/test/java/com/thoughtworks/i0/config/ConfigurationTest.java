package com.thoughtworks.i0.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.thoughtworks.i0.config.builder.ConfigurationBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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
    public void should_generate_jpa_properties_based_on_values() {
        Configuration config = new ConfigurationBuilder() {
            {
                database().driver("driver").url("url").user("user").password("password");
                http().port(8051);
            }
        }.build();

        Properties properties = config.getDatabase().toProperties();
        assertThat(properties.getProperty("javax.persistence.jdbc.driver"), is("driver"));
        assertThat(properties.getProperty("javax.persistence.jdbc.url"), is("url"));
        assertThat(properties.getProperty("javax.persistence.jdbc.user"), is("user"));
        assertThat(properties.getProperty("javax.persistence.jdbc.password"), is("password"));
    }

    @Test
    public void should_ignore_username_and_password_properties_if_any_of_them_is_null() {
        Configuration config = new ConfigurationBuilder() {
            {
                database().driver("driver").url("url").password("password");
                http().port(8051);
            }
        }.build();

        Properties properties = config.getDatabase().toProperties();
        assertThat(properties.getProperty("javax.persistence.jdbc.driver"), is("driver"));
        assertThat(properties.getProperty("javax.persistence.jdbc.url"), is("url"));
        assertThat(properties.getProperty("javax.persistence.jdbc.user"), is(nullValue()));
        assertThat(properties.getProperty("javax.persistence.jdbc.password"), is(nullValue()));
    }
}
