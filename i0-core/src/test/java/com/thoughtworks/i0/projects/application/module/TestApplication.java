package com.thoughtworks.i0.projects.application.module;

import com.thoughtworks.i0.Application;
import com.thoughtworks.i0.ApplicationModule;
import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.config.util.LogLevel;
import com.thoughtworks.i0.jetty.EmbeddedJetty;
import com.thoughtworks.i0.persist.PersistUnit;

import static com.thoughtworks.i0.config.Configuration.config;
import static com.thoughtworks.i0.persist.DatabaseConfiguration.database;

import com.thoughtworks.i0.persist.config.H2;

import com.thoughtworks.i0.persist.config.Hibernate;

@Application(name = "application")
@PersistUnit("domain")
@EmbeddedJetty
public class TestApplication extends ApplicationModule<TestConfiguration> {
    @Override
    protected TestConfiguration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return new TestConfiguration(config().logging()
                .level(LogLevel.INFO)
                .console().end()
                .end()
                .http()
                .end().build(), database().with(H2.driver, H2.tempFileDB, H2.compatible("ORACLE"),
                Hibernate.dialect("Oracle10g"), Hibernate.showSql).user("sa").password("").build());
    }
}
