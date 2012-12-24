package com.thoughtworks.i0.jpa.persist;


import com.thoughtworks.i0.config.Configuration;
import com.thoughtworks.i0.jpa.DatabaseConfiguration;
import com.thoughtworks.i0.jpa.WithDatabase;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class JpaConfiguration extends Configuration implements WithDatabase {
    private DatabaseConfiguration database;

    public JpaConfiguration() {
    }

    public JpaConfiguration(Configuration configuration, DatabaseConfiguration database) {
        super(configuration);
        this.database = database;
    }

    @XmlElement
    @Override
    public DatabaseConfiguration getDatabase() {
        return database;
    }
}
