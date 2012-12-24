package com.thoughtworks.i0.jetty.servlet;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.core.Application;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.Servlet3;
import com.thoughtworks.i0.jetty.EmbeddedJetty;

import static com.thoughtworks.i0.config.Configuration.config;

@Application("embedded")
@EmbeddedJetty
@Servlet3
public class EmbeddedContainer extends ApplicationModule<Configuration> {
    @Override
    protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return config().http().port(8051).end().logging().level(LogLevel.INFO).console().end().end().build();
    }
}
