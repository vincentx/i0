package com.thoughtworks.i0.core.guicemodule;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.GuiceModule;

@GuiceModule(packages = "com.thoughtworks.i0.core.guicemodule.p2")
public class Specified extends ApplicationModule<Configuration> {
    @Override
    protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return Configuration.config().http().end().build();
    }
}
