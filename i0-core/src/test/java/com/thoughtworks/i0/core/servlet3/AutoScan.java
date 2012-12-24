package com.thoughtworks.i0.core.servlet3;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.Application;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.Servlet3;

import static com.thoughtworks.i0.config.Configuration.config;

@Application("autoscan")
@Servlet3
public class AutoScan extends ApplicationModule<Configuration> {
    @Override
    protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return config().http().end().build();
    }
}
