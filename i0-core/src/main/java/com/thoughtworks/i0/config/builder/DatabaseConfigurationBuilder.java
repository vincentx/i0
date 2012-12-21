package com.thoughtworks.i0.config.builder;

import com.google.common.collect.ImmutableMap;
import com.thoughtworks.i0.config.DatabaseConfiguration;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Throwables.propagate;

public class DatabaseConfigurationBuilder implements Builder<DatabaseConfiguration> {
    private String driver;
    private String url;
    private StringBuffer urlAppendix = new StringBuffer();
    private String password;
    private String user;
    private ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
    private ConfigurationBuilder parent;

    public DatabaseConfigurationBuilder(ConfigurationBuilder parent) {
        this.parent = parent;
    }

    public DatabaseConfigurationBuilder driver(String driver) {
        this.driver = driver;
        return this;
    }

    public DatabaseConfigurationBuilder url(String url) {
        this.url = url;
        return this;
    }

    public DatabaseConfigurationBuilder password(String password) {
        this.password = password;
        return this;
    }

    public DatabaseConfigurationBuilder user(String user) {
        this.user = user;
        return this;
    }

    public DatabaseConfigurationBuilder property(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public DatabaseConfigurationBuilder with(Setting... settings) {
        for (Setting setting : settings) {
            setting.set(this);
        }
        return this;
    }

    public static abstract class Setting {
        public static Setting and(final Setting... settings) {
            return new Setting() {
                @Override
                public void set(DatabaseConfigurationBuilder config) {
                    for (Setting setting : settings) setting.set(config);
                }
            };
        }

        public abstract void set(DatabaseConfigurationBuilder config);
    }

    public static class H2 {
        public static final Setting driver = new Setting() {
            @Override
            public void set(DatabaseConfigurationBuilder config) {
                config.driver("org.h2.Driver");
            }
        };
        public static final Setting tempFile = new Setting() {
            @Override
            public void set(DatabaseConfigurationBuilder config) {
                try {
                    config.url("jdbc:driver:" + File.createTempFile("i0-db-driver", ".db").getAbsolutePath());
                } catch (IOException e) {
                    propagate(e);
                }
            }
        };

        public static final Setting privateMemoryDB = new Setting() {
            @Override
            public void set(DatabaseConfigurationBuilder config) {
                config.url("jdbc:h2:mem");
            }
        };


        public static Setting compatible(final String mode) {
            return new Setting() {
                @Override
                public void set(DatabaseConfigurationBuilder config) {
                    config.urlAppendix.append(";MODE=" + mode);
                }
            };
        }
    }

    public static class Hibernate {
        public static Setting dialect(final String name) {
            return new Setting() {
                @Override
                public void set(DatabaseConfigurationBuilder config) {
                    config.property("hibernate.dialect", "org.hibernate.dialect." + name + "Dialect");
                }
            };
        }

        public static Setting createDrop = new Setting() {
            @Override
            public void set(DatabaseConfigurationBuilder config) {
                config.property("hibernate.hbm2ddl.auto", "create-drop");
            }
        };
    }

    public ConfigurationBuilder end() {
        return parent;
    }

    public DatabaseConfiguration build() {
        return new DatabaseConfiguration(driver, url, password, user, properties.build());
    }
}