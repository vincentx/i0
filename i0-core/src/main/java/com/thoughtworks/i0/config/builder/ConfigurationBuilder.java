package com.thoughtworks.i0.config.builder;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.DatabaseConfiguration;
import com.thoughtworks.i0.config.HttpConfiguration;

public class ConfigurationBuilder {
    private final DatabaseConfigurationBuilder database = new DatabaseConfigurationBuilder();
    private final HttpConfigurationBuilder http = new HttpConfigurationBuilder();

    public DatabaseConfigurationBuilder database() {
        return database;
    }

    public HttpConfigurationBuilder http() {
        return http;
    }

    public Configuration build() {
        return new Configuration(database.build(), http.build());
    }
}