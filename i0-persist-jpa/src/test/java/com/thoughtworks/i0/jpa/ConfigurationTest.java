package com.thoughtworks.i0.jpa;

import com.thoughtworks.i0.jpa.config.H2;
import com.thoughtworks.i0.jpa.config.Hibernate;
import com.thoughtworks.i0.jpa.persist.JpaConfiguration;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.thoughtworks.i0.config.Configuration.read;
import static com.thoughtworks.i0.jpa.DatabaseConfiguration.database;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConfigurationTest {
    @Test
    public void should_load_database_configuration() throws IOException {
        JpaConfiguration configuration = read(fixture("database.yml"), JpaConfiguration.class);
        assertThat(configuration.getDatabase(), is(database().with(H2.driver, H2.compatible("ORACLE"),
                H2.privateMemoryDB, Hibernate.dialect("Oracle")).user("sa").password("")
                .migration()
                .auto(false)
                .locations("db/migration")
                .placeholder("user", "real_user")
                .end()
                .build()));

    }

    private InputStream fixture(String fixture) {
        return getClass().getResourceAsStream(fixture);
    }
}

