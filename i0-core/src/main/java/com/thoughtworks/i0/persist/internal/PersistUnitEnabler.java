package com.thoughtworks.i0.persist.internal;

import com.google.inject.Binder;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.thoughtworks.i0.core.BindingConfigurator;
import com.thoughtworks.i0.core.FacetEnabler;
import com.thoughtworks.i0.core.StartupTasks;
import com.thoughtworks.i0.persist.PersistUnit;
import com.thoughtworks.i0.persist.WithDatabase;
import com.thoughtworks.i0.persist.internal.migration.Migration;

import static com.google.common.base.Preconditions.checkArgument;

public class PersistUnitEnabler implements StartupTasks<PersistUnit, WithDatabase>, BindingConfigurator<PersistUnit, WithDatabase> {

    @Override
    public void configure(Binder binder, PersistUnit annotation, WithDatabase configuration) {
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
    public void perform(PersistUnit annotation, WithDatabase configuration) {
        if (configuration.getDatabase() != null) Migration.migrate(configuration.getDatabase());
    }
}
