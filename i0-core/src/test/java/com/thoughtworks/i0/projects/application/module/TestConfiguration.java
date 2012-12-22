package com.thoughtworks.i0.projects.application.module;

import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.persist.WithDatabase;
import com.thoughtworks.i0.persist.config.DatabaseConfiguration;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class TestConfiguration extends Configuration implements WithDatabase {
    @NotNull
    private DatabaseConfiguration database;

    private TestConfiguration() {
    }

    public TestConfiguration(Configuration configuration, DatabaseConfiguration database) {
        super(configuration);
        this.database = database;
    }

    @XmlElement
    public DatabaseConfiguration getDatabase() {
        return database;
    }
}
