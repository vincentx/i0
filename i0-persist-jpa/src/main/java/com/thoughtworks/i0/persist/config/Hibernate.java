package com.thoughtworks.i0.persist.config;

import static com.thoughtworks.i0.persist.DatabaseConfiguration.DatabaseConfigurationBuilder;

public class Hibernate {
    public static DatabaseConfigurationBuilder.Setting dialect(final String name) {
        return new DatabaseConfigurationBuilder.Setting() {
            @Override
            public void set(DatabaseConfigurationBuilder config) {
                config.property("hibernate.dialect", "org.hibernate.dialect." + name + "Dialect");
            }
        };
    }

    public static DatabaseConfigurationBuilder.Setting createDrop = new DatabaseConfigurationBuilder.Setting() {
        @Override
        public void set(DatabaseConfigurationBuilder config) {
            config.property("hibernate.hbm2ddl.auto", "create-drop");
        }
    };

    public static DatabaseConfigurationBuilder.Setting showSql = new DatabaseConfigurationBuilder.Setting() {
        @Override
        public void set(DatabaseConfigurationBuilder config) {
            config.property("hibernate.show_sql", "true");
        }
    };
}
