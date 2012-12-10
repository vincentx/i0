package com.thoughtworks.i0.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thoughtworks.i0.config.builder.HttpConfigurationBuilder;

import java.util.HashMap;

public class Configuration {
    @JsonProperty("database")
    private DatabaseConfiguration database;

    @JsonProperty("http")
    private HttpConfiguration http;

    @JsonCreator
    public Configuration(@JsonProperty("database") DatabaseConfiguration database,
                         @JsonProperty("http") HttpConfiguration http) {
        this.database = database != null ? database : new DatabaseConfiguration("", "", "", "", new HashMap<String, String>());
        this.http = http != null ? http : new HttpConfiguration(8080);
    }

    public DatabaseConfiguration getDatabase() {
        return database;
    }

    public HttpConfiguration getHttp() {
        return http;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        if (database != null ? !database.equals(that.database) : that.database != null) return false;
        if (http != null ? !http.equals(that.http) : that.http != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = database != null ? database.hashCode() : 0;
        result = 31 * result + (http != null ? http.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "database=" + database +
                ", http=" + http +
                '}';
    }
}
