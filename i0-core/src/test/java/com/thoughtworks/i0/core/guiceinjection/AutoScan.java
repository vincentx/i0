package com.thoughtworks.i0.core.guiceinjection;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.Application;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.GuiceInjection;

@GuiceInjection
@Application("autoscan")
public class AutoScan extends ApplicationModule<Configuration> {
    @Override
    protected Configuration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return Configuration.config().http().end().build();
    }
}
