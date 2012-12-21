package com.thoughtworks.i0.config;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Optional.absent;

@XmlType
public class DatabaseConfiguration {
    @NotNull
    private String driver;
    @NotNull
    private String url;
    @NotNull
    private String password;
    @NotNull
    private String user;
    @NotNull
    private Map<String, String> properties = ImmutableMap.of();
    private Optional<MigrationConfiguration> migration = absent();

    private DatabaseConfiguration() {
    }

    public DatabaseConfiguration(String driver, String url, String password, String user, Map<String, String> properties, Optional<MigrationConfiguration> migration) {
        this.driver = driver;
        this.url = url;
        this.password = password;
        this.user = user;
        this.properties = properties;
        this.migration = migration;
    }

    @XmlElement
    public String getDriver() {
        return driver;
    }

    @XmlElement
    public String getUrl() {
        return url;
    }

    @XmlElement
    public String getPassword() {
        return password;
    }

    @XmlElement
    public String getUser() {
        return user;
    }

    @XmlElement
    public Map<String, String> getProperties() {
        return properties;
    }

    @XmlElement
    public Optional<MigrationConfiguration> getMigration() {
        return migration;
    }

    public Properties toProperties() {
        Properties properties = new Properties();

        if (driver != null) properties.put("javax.persistence.jdbc.driver", driver);
        if (url != null) properties.put("javax.persistence.jdbc.url", url);
        if (password != null && user != null) {
            properties.put("javax.persistence.jdbc.user", user);
            properties.put("javax.persistence.jdbc.password", password);
        }
        if (this.properties != null)
            for (String key : this.properties.keySet())
                properties.put(key, this.properties.get(key));
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatabaseConfiguration that = (DatabaseConfiguration) o;

        if (!driver.equals(that.driver)) return false;
        if (!password.equals(that.password)) return false;
        if (!properties.equals(that.properties)) return false;
        if (!url.equals(that.url)) return false;
        if (!user.equals(that.user)) return false;
        if (!migration.equals(that.migration)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = driver.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + properties.hashCode();
        result = 31 * result + migration.hashCode();
        return result;
    }

    @XmlType
    public static class MigrationConfiguration {
        private boolean auto = true;
        private String[] locations = new String[]{"db/migration"};
        private Map<String, String> placeholders = ImmutableMap.of();

        private MigrationConfiguration() {
        }

        public MigrationConfiguration(boolean auto, String[] locations, Map<String, String> placeholders) {
            this.auto = auto;
            this.locations = locations;
            this.placeholders = placeholders;
        }

        @XmlElement
        public boolean isAuto() {
            return auto;
        }

        @XmlElement
        public String[] getLocations() {
            return locations;
        }

        @XmlElement
        public Map<String, String> getPlaceholders() {
            return placeholders;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MigrationConfiguration that = (MigrationConfiguration) o;

            if (auto != that.auto) return false;
            if (!Arrays.equals(locations, that.locations)) return false;
            if (!placeholders.equals(that.placeholders)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (auto ? 1 : 0);
            result = 31 * result + Arrays.hashCode(locations);
            result = 31 * result + placeholders.hashCode();
            return result;
        }
    }
}
