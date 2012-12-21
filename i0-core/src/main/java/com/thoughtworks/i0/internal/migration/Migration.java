package com.thoughtworks.i0.internal.migration;

import com.googlecode.flyway.core.Flyway;
import com.thoughtworks.i0.config.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Migration {
    private static Logger logger = LoggerFactory.getLogger(Migration.class);
    private static Flyway flyway = new Flyway();

    public static void migrate(DatabaseConfiguration config) {
        if (config.getMigration().isPresent()) {
            DatabaseConfiguration.MigrationConfiguration configuration = config.getMigration().get();
            flyway.setLocations(configuration.getLocations());
            flyway.setPlaceholders(configuration.getPlaceholders());
            if (!configuration.isAuto()) {
                logger.info("Automatic database migration is disabled");
                return;
            }
        }
        flyway.setValidateOnMigrate(true);
        flyway.configure(flywayConfiguration(config));
        flyway.migrate();
    }

    private static Properties flywayConfiguration(DatabaseConfiguration configuration) {
        Properties properties = new Properties();
        properties.put("flyway.driver", configuration.getDriver());
        properties.put("flyway.url", configuration.getUrl());
        properties.put("flyway.user", configuration.getUser());
        properties.put("flyway.password", configuration.getPassword());
        return properties;
    }
}
