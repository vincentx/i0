package com.thoughtworks.i0.jersey.api;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.core.Application;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.jersey.RestApi;
import com.thoughtworks.i0.jetty.EmbeddedJetty;

import static com.thoughtworks.i0.config.Configuration.config;

@Application("autoscan") @EmbeddedJetty @RestApi
public class AutoScan extends ApplicationModule<Configuration> {
    @Override
    protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return config().http().port(8051).end().logging().level(LogLevel.INFO).console().end().end().build();
    }
}
