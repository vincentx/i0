package com.thoughtworks.i0.projects.application.module;

import com.thoughtworks.i0.Application;
import com.thoughtworks.i0.ApplicationModule;
import com.thoughtworks.i0.PersistUnit;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.builder.ConfigurationBuilder;
import com.thoughtworks.i0.config.util.LogLevel;

import static com.thoughtworks.i0.config.builder.DatabaseConfigurationBuilder.H2;
import static com.thoughtworks.i0.config.builder.DatabaseConfigurationBuilder.Hibernate;

@Application(name = "application")
@PersistUnit("domain")
public class ApplicationModuleTestApplication extends ApplicationModule {
    @Override
    protected Configuration getDefaultConfiguration(ConfigurationBuilder config) {
        return config
                .logging()
                .level(LogLevel.INFO)
                .console().end()
                .end()
                .http()
                .end()
                .database().with(H2.driver, H2.privateMemoryDB, H2.compatible("ORACLE"),
                        Hibernate.dialect("Oracle"), Hibernate.createDrop).user("sa").password("")
                .end().build();
    }
}
