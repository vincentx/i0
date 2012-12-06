package com.thoughtworks.i0.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class EnvironmentConfiguration {
    @JsonProperty("api")
    private Set<String> apiPackages;
    @JsonProperty("servlets")
    private Set<String> servletPackages;
    @JsonProperty("filters")
    private Set<String> filterPackages;
    @JsonProperty("database")
    private DatabaseConfiguration databaseConfiguration;

    @JsonCreator
    public EnvironmentConfiguration(
            @JsonProperty("api") Set<String> apiPackages,
            @JsonProperty("servlets") Set<String> servletPackages,
            @JsonProperty("filters") Set<String> filterPackages,
            @JsonProperty("database") DatabaseConfiguration databaseConfiguration) {
        this.apiPackages = apiPackages;
        this.servletPackages = servletPackages;
        this.filterPackages = filterPackages;
        this.databaseConfiguration = databaseConfiguration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnvironmentConfiguration that = (EnvironmentConfiguration) o;

        if (apiPackages != null ? !apiPackages.equals(that.apiPackages) : that.apiPackages != null) return false;
        if (databaseConfiguration != null ? !databaseConfiguration.equals(that.databaseConfiguration) : that.databaseConfiguration != null)
            return false;
        if (filterPackages != null ? !filterPackages.equals(that.filterPackages) : that.filterPackages != null)
            return false;
        if (servletPackages != null ? !servletPackages.equals(that.servletPackages) : that.servletPackages != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = apiPackages != null ? apiPackages.hashCode() : 0;
        result = 31 * result + (servletPackages != null ? servletPackages.hashCode() : 0);
        result = 31 * result + (filterPackages != null ? filterPackages.hashCode() : 0);
        result = 31 * result + (databaseConfiguration != null ? databaseConfiguration.hashCode() : 0);
        return result;
    }
}
