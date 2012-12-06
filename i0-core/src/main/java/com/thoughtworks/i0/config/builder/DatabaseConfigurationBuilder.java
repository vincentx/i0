package com.thoughtworks.i0.config.builder;

import com.thoughtworks.i0.config.DatabaseConfiguration;

import java.util.Map;

public class DatabaseConfigurationBuilder {
    private String driver;
    private String url;
    private String user;
    private String password;
    private Map<String, String> properties;

    public DatabaseConfigurationBuilder driver(String driver) {
        this.driver = driver;
        return this;
    }

    public DatabaseConfigurationBuilder url(String url) {
        this.url = url;
        return this;
    }

    public DatabaseConfigurationBuilder user(String user) {
        this.user = user;
        return this;
    }

    public DatabaseConfigurationBuilder password(String password) {
        this.password = password;
        return this;
    }

    public DatabaseConfigurationBuilder properties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    public DatabaseConfiguration build() {
        return new DatabaseConfiguration(driver, url, user, password, properties);
    }
}