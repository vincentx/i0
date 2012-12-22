package com.thoughtworks.i0.persist;

import com.google.inject.Binder;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.facet.FacetEnabler;
import com.thoughtworks.i0.internal.migration.Migration;

import static com.google.common.base.Preconditions.checkArgument;

public class PersistUnitEnabler implements FacetEnabler<PersistUnit> {
    @Override
    public void createBindings(Binder binder, PersistUnit annotation, Configuration configuration) {
        checkArgument(configuration.getDatabase().isPresent(), "No database configuration found");
        binder.install(new JpaPersistModule(annotation.value()).properties(configuration.getDatabase().get().toProperties()));
        binder.install(new ServletModule() {
            @Override
            protected void configureServlets() {
                filter("/*").through(PersistFilter.class);
            }
        });
    }

    @Override
    public void performPreLaunchTasks(PersistUnit annotation, Configuration configuration) {
        if (configuration.getDatabase().isPresent()) Migration.migrate(configuration.getDatabase().get());
    }

}
