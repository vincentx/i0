package com.thoughtworks.i0.config.builder;

import com.thoughtworks.i0.config.HttpConfiguration;

public class HttpConfigurationBuilder {
    private int port;

    public HttpConfigurationBuilder port(int port) {
        this.port = port;
        return this;
    }

    public HttpConfiguration build() {
        return new HttpConfiguration(port);
    }
}