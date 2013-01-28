package com.thoughtworks.i0.jersey.internal;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.BindingProvider;
import com.thoughtworks.i0.jersey.RestApi;

import static com.google.common.base.Joiner.on;
import static com.sun.jersey.api.core.PackagesResourceConfig.PROPERTY_PACKAGES;
import static com.sun.jersey.api.json.JSONConfiguration.FEATURE_POJO_MAPPING;

public class JerseyEnabler implements BindingProvider<RestApi, Configuration> {

    @Override
    public void configure(Binder binder, final RestApi annotation, ApplicationModule<?> module, Configuration configuration) {
        final String[] autoScanPackages = new String[]{module.getClass().getPackage().getName()};
        binder.install(new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                ImmutableSet<String> packageSet = ImmutableSet.<String>builder()
                        .add(annotation.packages().length == 0 ? autoScanPackages : annotation.packages()).build();
                serve(annotation.prefix()).with(GuiceContainer.class, new ImmutableMap.Builder<String, String>()
                        .put(PROPERTY_PACKAGES, on(";").skipNulls().join(packageSet)).put(FEATURE_POJO_MAPPING, "true").build());
            }
        });
    }
}
