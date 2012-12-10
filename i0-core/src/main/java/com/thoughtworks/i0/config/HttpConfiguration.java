package com.thoughtworks.i0.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpConfiguration {
    @JsonProperty
    private int port = 8080;

    @JsonCreator
    public HttpConfiguration(@JsonProperty("port") int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HttpConfiguration that = (HttpConfiguration) o;

        if (port != that.port) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return port;
    }
}
