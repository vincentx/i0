package com.thoughtworks.i0.core;

import com.thoughtworks.i0.config.Configuration;

@Application("")
public class CompositeApplicationModule<T extends Configuration> extends ApplicationModule {
    private ApplicationModule[] subModules;

    public CompositeApplicationModule(ApplicationModule... subModules) {
        this.subModules = subModules;
    }

    public ApplicationModule[] getSubModules() {
        return subModules;
    }
}
