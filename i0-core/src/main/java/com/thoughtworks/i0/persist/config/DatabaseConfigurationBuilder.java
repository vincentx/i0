package com.thoughtworks.i0.persist.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.thoughtworks.i0.config.builder.Builder;
import com.thoughtworks.i0.config.builder.OptionalBuilder;

import java.io.File;
import java.io.IOException;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.toArray;
import static com.thoughtworks.i0.persist.config.DatabaseConfiguration.MigrationConfiguration;

public class DatabaseConfigurationBuilder implements Builder<DatabaseConfiguration> {
    private String driver;
    private String url;
    private StringBuffer urlAppendix = new StringBuffer();
    private String password;
    private String user;
    private ImmutableMap.Builder<String, String> properties = ImmutableMap.builder();
    private OptionalBuilder<MigrationConfigurationBuilder, MigrationConfiguration>
            migration = new OptionalBuilder<>(new MigrationConfigurationBuilder());

    DatabaseConfigurationBuilder() {
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

    public MigrationConfigurationBuilder migration() {
        return migration.builder();
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
        public static final Setting tempFileDB = new Setting() {
            @Override
            public void set(DatabaseConfigurationBuilder config) {
                try {
                    config.url("jdbc:h2:" + File.createTempFile("i0-db-driver", ".db").getAbsolutePath());
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

        public static Setting showSql = new Setting() {
            @Override
            public void set(DatabaseConfigurationBuilder config) {
                config.property("hibernate.show_sql", "true");
            }
        };
    }

    public DatabaseConfiguration build() {
        return new DatabaseConfiguration(driver, url, password, user, properties.build(), migration.build());
    }

    public class MigrationConfigurationBuilder implements Builder<DatabaseConfiguration.MigrationConfiguration> {
        private boolean auto = true;
        private ImmutableSet.Builder<String> locations = ImmutableSet.builder();
        private ImmutableMap.Builder<String, String> placeholders = ImmutableMap.builder();

        public MigrationConfigurationBuilder auto(boolean auto) {
            this.auto = auto;
            return this;
        }

        public MigrationConfigurationBuilder locations(String... locations) {
            this.locations.add(locations);
            return this;
        }

        public MigrationConfigurationBuilder placeholder(String placeholder, String value) {
            placeholders.put(placeholder, value);
            return this;
        }

        public DatabaseConfigurationBuilder end() {
            return DatabaseConfigurationBuilder.this;
        }

        public MigrationConfiguration build() {
            return new MigrationConfiguration(auto, toArray(locations.build(), String.class), placeholders.build());
        }
    }
}