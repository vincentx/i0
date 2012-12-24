package com.thoughtworks.i0.core.internal.servlet;

import com.google.inject.Binder;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.BindingProvider;
import com.thoughtworks.i0.core.Servlet3;

public class Servlet3Enabler implements BindingProvider<Servlet3, Configuration> {
    @Override
    public void configure(Binder binder, Servlet3 annotation, ApplicationModule<?> module, Configuration configuration) {
        final String[] autoScanPackages = new String[]{module.getClass().getPackage().getName()};
        binder.install(new AutoScanningServletModule(annotation.packages().length == 0 ? autoScanPackages : annotation.packages()));
    }
}
