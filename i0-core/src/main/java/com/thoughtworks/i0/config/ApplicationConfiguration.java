package com.thoughtworks.i0.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Set;

public class ApplicationConfiguration {
    @JsonProperty("persist-unit")
    private String persistUnit;
    @JsonProperty("root")
    private String rootPackage;
    @JsonProperty("api")
    private Set<String> apiPackages;
    @JsonProperty("servlets")
    private Set<String> servletPackages;
    @JsonProperty("filters")
    private Set<String> filterPackages;
    @JsonProperty("service-modules")
    private Set<String> servicePackages;
    @JsonProperty("env")
    private Map<String, EnvironmentConfiguration> environments;

    @JsonCreator
    public ApplicationConfiguration(
            @JsonProperty("persist-unit") String persistUnit,
            @JsonProperty("root") String rootPackage,
            @JsonProperty("api") Set<String> apiPackages,
            @JsonProperty("servlets") Set<String> servletPackages,
            @JsonProperty("filters") Set<String> filterPackages,
            @JsonProperty("service-module") Set<String> servicePackages,
            @JsonProperty("env") Map<String, EnvironmentConfiguration> environments) {
        this.persistUnit = persistUnit;
        this.rootPackage = rootPackage;
        this.apiPackages = apiPackages;
        this.servletPackages = servletPackages;
        this.filterPackages = filterPackages;
        this.servicePackages = servicePackages;
        this.environments = environments;
    }

    public String getPersistUnit() {
        return persistUnit;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public Set<String> getApiPackages() {
        return apiPackages;
    }

    public Set<String> getServletPackages() {
        return servletPackages;
    }

    public Set<String> getFilterPackages() {
        return filterPackages;
    }

    public Map<String, EnvironmentConfiguration> getEnvironments() {
        return environments;
    }

    public Set<String> getServicePackages() {
        return servicePackages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationConfiguration that = (ApplicationConfiguration) o;

        if (apiPackages != null ? !apiPackages.equals(that.apiPackages) : that.apiPackages != null) return false;
        if (environments != null ? !environments.equals(that.environments) : that.environments != null) return false;
        if (filterPackages != null ? !filterPackages.equals(that.filterPackages) : that.filterPackages != null)
            return false;
        if (persistUnit != null ? !persistUnit.equals(that.persistUnit) : that.persistUnit != null) return false;
        if (rootPackage != null ? !rootPackage.equals(that.rootPackage) : that.rootPackage != null) return false;
        if (servicePackages != null ? !servicePackages.equals(that.servicePackages) : that.servicePackages != null)
            return false;
        if (servletPackages != null ? !servletPackages.equals(that.servletPackages) : that.servletPackages != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = persistUnit != null ? persistUnit.hashCode() : 0;
        result = 31 * result + (rootPackage != null ? rootPackage.hashCode() : 0);
        result = 31 * result + (apiPackages != null ? apiPackages.hashCode() : 0);
        result = 31 * result + (servletPackages != null ? servletPackages.hashCode() : 0);
        result = 31 * result + (filterPackages != null ? filterPackages.hashCode() : 0);
        result = 31 * result + (servicePackages != null ? servicePackages.hashCode() : 0);
        result = 31 * result + (environments != null ? environments.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" +
                "persistUnit='" + persistUnit + '\'' +
                ", rootPackage='" + rootPackage + '\'' +
                ", apiPackages=" + apiPackages +
                ", servletPackages=" + servletPackages +
                ", filterPackages=" + filterPackages +
                ", servicePackages=" + servicePackages +
                ", environments=" + environments +
                '}';
    }
}
