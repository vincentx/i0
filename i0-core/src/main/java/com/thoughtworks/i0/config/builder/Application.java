package com.thoughtworks.i0.config.builder;

import com.thoughtworks.i0.config.ApplicationConfiguration;

public class Application {
    private ApplicationConfigurationBuilder configurationBuilder;

    protected ApplicationConfigurationBuilder config() {
        if (configurationBuilder == null) configurationBuilder = new ApplicationConfigurationBuilder();
        return configurationBuilder;
    }

    public ApplicationConfiguration configuration() {
        return configurationBuilder.build();
    }
}
