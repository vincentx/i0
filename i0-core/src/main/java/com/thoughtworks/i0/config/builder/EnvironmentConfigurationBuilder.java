package com.thoughtworks.i0.config.builder;

import com.thoughtworks.i0.config.EnvironmentConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EnvironmentConfigurationBuilder {
    private Set<String> apiPackages = new HashSet<>();
    private Set<String> servletPackages = new HashSet<>();
    private Set<String> filterPackages = new HashSet<>();
    private Set<String> servicePackages = new HashSet<>();
    private DatabaseConfigurationBuilder databaseConfigurationBuilder;

    public EnvironmentConfigurationBuilder api(String... apiPackages) {
        Collections.addAll(this.apiPackages, apiPackages);
        return this;
    }

    public EnvironmentConfigurationBuilder servlets(String... servletPackages) {
        Collections.addAll(this.servletPackages, servletPackages);
        return this;
    }

    public EnvironmentConfigurationBuilder filters(String... filterPackages) {
        Collections.addAll(this.filterPackages, filterPackages);
        return this;
    }

    public EnvironmentConfigurationBuilder services(String... servicePackages) {
        Collections.addAll(this.servicePackages, servicePackages);
        return this;
    }

    public DatabaseConfigurationBuilder database() {
        this.databaseConfigurationBuilder = new DatabaseConfigurationBuilder();
        return databaseConfigurationBuilder;
    }


    public EnvironmentConfiguration build() {
        return new EnvironmentConfiguration(apiPackages, servletPackages, filterPackages, servicePackages, databaseConfigurationBuilder.build());
    }
}