package com.thoughtworks.i0.jpa.persist;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.core.Application;
import com.thoughtworks.i0.core.ApplicationModule;
import com.thoughtworks.i0.core.Servlet3;
import com.thoughtworks.i0.jetty.EmbeddedJetty;
import com.thoughtworks.i0.jpa.JpaPersist;
import com.thoughtworks.i0.jpa.config.H2;
import com.thoughtworks.i0.jpa.config.Hibernate;

import static com.thoughtworks.i0.config.Configuration.config;
import static com.thoughtworks.i0.jpa.DatabaseConfiguration.database;

@Application("jpa")
@JpaPersist(unit = "domain")
@EmbeddedJetty
@Servlet3
public class JpaModule extends ApplicationModule<JpaConfiguration> {
    @Override
    protected JpaConfiguration createDefaultConfiguration(Configuration.ConfigurationBuilder config) {
        return new JpaConfiguration(config().logging().console().end().end().build(),
                database().with(H2.driver, H2.tempFileDB, H2.compatible("ORACLE"),
                        Hibernate.dialect("Oracle10g"), Hibernate.showSql)
                        .user("sa").password("").build());
    }
}
