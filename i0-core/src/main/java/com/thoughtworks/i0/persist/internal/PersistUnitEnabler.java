package com.thoughtworks.i0.persist.internal;

import com.google.inject.Binder;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.thoughtworks.i0.facet.FacetEnabler;
import com.thoughtworks.i0.persist.PersistUnit;
import com.thoughtworks.i0.persist.WithDatabase;
import com.thoughtworks.i0.persist.internal.migration.Migration;

import static com.google.common.base.Preconditions.checkArgument;

public class PersistUnitEnabler implements FacetEnabler<PersistUnit, WithDatabase> {
    @Override
    public void createBindings(Binder binder, PersistUnit annotation, WithDatabase configuration) {
        checkArgument(configuration.getDatabase() != null, "No database configuration found");
        binder.install(new JpaPersistModule(annotation.value()).properties(configuration.getDatabase().toProperties()));
        binder.install(new ServletModule() {
            @Override
            protected void configureServlets() {
                filter("/*").through(PersistFilter.class);
            }
        });
    }

    @Override
    public void performPreLaunchTasks(PersistUnit annotation, WithDatabase configuration) {
        if (configuration.getDatabase() != null) Migration.migrate(configuration.getDatabase());
    }

}
