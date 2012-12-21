package com.thoughtworks.i0.config.builder;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.DatabaseConfiguration;

public class ConfigurationBuilder implements Builder<Configuration> {
    public static ConfigurationBuilder config() {
        return new ConfigurationBuilder();
    }

    private HttpConfigurationBuilder http = new HttpConfigurationBuilder(this);

    private LoggingConfigurationBuilder logging = new LoggingConfigurationBuilder(this);

    private OptionalBuilder<DatabaseConfigurationBuilder, DatabaseConfiguration> database =
            new OptionalBuilder(new DatabaseConfigurationBuilder(this));

    private ConfigurationBuilder() {
    }

    public HttpConfigurationBuilder http() {
        return http;
    }

    public LoggingConfigurationBuilder logging() {
        return logging;
    }

    public DatabaseConfigurationBuilder database() {
        return database.builder();
    }

    @Override
    public Configuration build() {
        return new Configuration(http.build(), logging.build(), database.build());
    }
}
