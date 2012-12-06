package com.thoughtworks.i0.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class DatabaseConfiguration {
    private static final List<String> jpaProperties = asList("lock.timeout",
            "query.timeout", "validation.mode", "validation.group.pre-persist", "validation.group.pre-update",
            "validation.group.pre-remove", "provider");

    @JsonProperty
    private String driver;
    @JsonProperty
    private String url;
    @JsonProperty
    private String user;
    @JsonProperty
    private String password;
    @JsonProperty
    private Map<String, String> properties = new HashMap<>();

    @JsonCreator
    public DatabaseConfiguration(
            @JsonProperty("driver") String driver,
            @JsonProperty("url") String url,
            @JsonProperty("user") String user,
            @JsonProperty("password") String password,
            @JsonProperty("properties") Map<String, String> properties) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseConfiguration that = (DatabaseConfiguration) o;

        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = driver != null ? driver.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
