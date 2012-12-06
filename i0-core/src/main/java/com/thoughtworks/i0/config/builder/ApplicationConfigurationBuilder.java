package com.thoughtworks.i0.config.builder;

import com.thoughtworks.i0.config.ApplicationConfiguration;
import com.thoughtworks.i0.config.EnvironmentConfiguration;

import java.util.*;

public class ApplicationConfigurationBuilder {
    private String persistUnit;
    private String rootPackage;
    private Set<String> apiPackages = new HashSet<>();
    private Set<String> servletPackages = new HashSet<>();
    private Set<String> filterPackages = new HashSet<>();
    private Map<String, EnvironmentConfigurationBuilder> environmentBuilders = new HashMap<>();

    public ApplicationConfigurationBuilder unit(String persistUnit) {
        this.persistUnit = persistUnit;
        return this;
    }

    public ApplicationConfigurationBuilder root(String rootPackage) {
        this.rootPackage = rootPackage;
        return this;
    }

    public ApplicationConfigurationBuilder api(String... apiPackages) {
        Collections.addAll(this.apiPackages, apiPackages);
        return this;
    }

    public ApplicationConfigurationBuilder servlets(String... servletPackages) {
        Collections.addAll(this.servletPackages, servletPackages);
        return this;
    }

    public ApplicationConfigurationBuilder filters(String... filterPackages) {
        Collections.addAll(this.filterPackages, filterPackages);
        return this;
    }

    public EnvironmentConfigurationBuilder environment(String name) {
        if (!environmentBuilders.containsKey(name))
            environmentBuilders.put(name, new EnvironmentConfigurationBuilder());
        return environmentBuilders.get(name);
    }

    public ApplicationConfiguration build() {
        Map<String, EnvironmentConfiguration> environments = new HashMap<>();
        for (String key : environmentBuilders.keySet()) environments.put(key, environmentBuilders.get(key).build());
        return new ApplicationConfiguration(persistUnit, rootPackage, apiPackages, servletPackages, filterPackages, environments);
    }
}