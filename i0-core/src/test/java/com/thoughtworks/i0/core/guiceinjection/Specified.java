package com.thoughtworks.i0.core.guiceinjection;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.GuiceInjection;

@GuiceInjection(packages = "com.thoughtworks.i0.core.guiceinjection.p2")
public class Specified extends ApplicationModule<Configuration> {
    @Override
    protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return Configuration.config().http().end().build();
    }
}
