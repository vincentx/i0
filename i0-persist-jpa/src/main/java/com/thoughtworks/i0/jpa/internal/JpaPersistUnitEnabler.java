package com.thoughtworks.i0.jpa.internal;

import com.google.inject.Binder;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.BindingProvider;
import com.thoughtworks.i0.core.StartupTasks;
import com.thoughtworks.i0.jpa.JpaPersist;
import com.thoughtworks.i0.jpa.WithDatabase;
import com.thoughtworks.i0.jpa.internal.migration.Migration;

import static com.google.common.base.Preconditions.checkArgument;

public class JpaPersistUnitEnabler implements StartupTasks<JpaPersist, WithDatabase>, BindingProvider<JpaPersist, WithDatabase> {

    @Override
    public void perform(JpaPersist annotation, WithDatabase configuration) {
        if (configuration.getDatabase() != null) Migration.migrate(configuration.getDatabase());
    }

    @Override
    public void configure(Binder binder, JpaPersist annotation, ApplicationModule<?> module, WithDatabase configuration) {
        checkArgument(configuration.getDatabase() != null, "No database configuration found");
        binder.install(new JpaPersistModule(annotation.unit()).properties(configuration.getDatabase().toProperties()));
        binder.install(new ServletModule() {
            @Override
            protected void configureServlets() {
                filter("/*").through(PersistFilter.class);
            }
        });
    }
}
