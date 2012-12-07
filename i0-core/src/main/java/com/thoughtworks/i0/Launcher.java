package com.thoughtworks.i0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.thoughtworks.i0.config.ApplicationConfiguration;
import com.thoughtworks.i0.config.DatabaseConfiguration;
import com.thoughtworks.i0.config.EnvironmentConfiguration;
import com.thoughtworks.i0.guice.PackageScanningServiceModule;
import com.thoughtworks.i0.guice.PackageScanningServletModule;
import com.thoughtworks.i0.server.JettyServer;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;

public class Launcher {
    private ApplicationConfiguration configuration;

    private String environment;

    private int port;
    private String name;

    public Launcher(String name, ApplicationConfiguration configuration, String environment, int port) {
        checkArgument(environment == null || configuration.getEnvironments().containsKey(environment), "Environment '" + environment + "' not defined!");
        this.name = name;
        this.configuration = configuration;
        this.environment = environment;
        this.port = port;
    }

    public JettyServer launch(boolean standalone) throws Exception {
        EnvironmentConfiguration environmentConfiguration = configuration.getEnvironments().get(environment);
        Set<Module> modules = new HashSet<>();
        web(modules, environmentConfiguration);
        api(modules, environmentConfiguration);
        persist(modules, environmentConfiguration);
        service(modules, environmentConfiguration);
        JettyServer server = new JettyServer(name, port, Iterables.toArray(modules, Module.class));
        server.start(standalone);
        return server;
    }

    private void web(Set<Module> modules, EnvironmentConfiguration environment) {
        Set<String> packages = webPackages(environment);
        if (packages.size() == 0) return;
        modules.add(new PackageScanningServletModule(Iterables.toArray(packages, String.class)));
    }

    private Set<String> webPackages(EnvironmentConfiguration environment) {
        Set<String> packages = new HashSet<>();
        if (configuration.getRootPackage() != null) {
            packages.add(configuration.getRootPackage() + ".servlets");
            packages.add(configuration.getRootPackage() + ".filters");
        }
        if (configuration.getServletPackages() != null) packages.addAll(configuration.getServletPackages());
        if (configuration.getFilterPackages() != null) packages.addAll(configuration.getFilterPackages());
        if (environment == null) return packages;
        if (environment.getServicePackages() != null) packages.addAll(environment.getServletPackages());
        if (environment.getFilterPackages() != null) packages.addAll(environment.getFilterPackages());
        return packages;
    }

    private void api(Set<Module> modules, EnvironmentConfiguration environment) {
        final Set<String> packages = apiPackages(environment);
        if (packages.size() == 1) return;
        modules.add(new ServletModule() {
            @Override
            protected void configureServlets() {
                serve("/api/*").with(GuiceContainer.class, new ImmutableMap.Builder<String, String>()
                        .put(PackagesResourceConfig.PROPERTY_PACKAGES, on(";").skipNulls().join(packages)).build());
            }
        });
    }

    private Set<String> apiPackages(EnvironmentConfiguration environment) {
        final Set<String> packages = new HashSet<>();
        if (configuration.getRootPackage() != null) packages.add(configuration.getRootPackage() + ".api");
        packages.add("com.fasterxml.jackson.jaxrs.json");
        if (configuration.getApiPackages() != null) packages.addAll(configuration.getApiPackages());
        if (environment != null && environment.getApiPackages() != null) packages.addAll(environment.getApiPackages());
        return packages;
    }

    private void persist(Set<Module> modules, EnvironmentConfiguration environment) {
        DatabaseConfiguration database = environment.getDatabaseConfiguration();
        if (database == null) return;
        Properties properties = new Properties();
        properties.put("javax.persistence.jdbc.driver", database.getDriver());
        properties.put("javax.persistence.jdbc.url", database.getUrl());
        if (database.getPassword() != null && !database.getPassword().isEmpty() &&
                database.getUser() != null && !database.getUser().isEmpty()) {
            properties.put("javax.persistence.jdbc.user", database.getUser());
            properties.put("javax.persistence.jdbc.password", database.getPassword());
        }
        for (String key : database.getProperties().keySet())
            if (DatabaseConfiguration.jpaProperties.contains(key))
                properties.put("javax.persistence." + key, database.getProperties().get(key));
            else
                properties.put(key, database.getProperties().get(key));
        modules.add(new JpaPersistModule(configuration.getPersistUnit()).properties(properties));
    }

    private void service(Set<Module> modules, EnvironmentConfiguration environment) {
        Set<String> packages = servicePackages(environment);
        if (packages.size() == 0) return;
        modules.add(new PackageScanningServiceModule(Iterables.toArray(packages, String.class)));
    }

    private Set<String> servicePackages(EnvironmentConfiguration environment) {
        final Set<String> packages = new HashSet<>();
        if (configuration.getRootPackage() != null)
            packages.add(configuration.getRootPackage() + ".services");
        if (configuration.getServletPackages() != null) packages.addAll(configuration.getServletPackages());
        if (environment.getServicePackages() != null) packages.addAll(environment.getServicePackages());
        return packages;
    }

    public static void main(String... arguments) throws Exception {
        if (arguments.length < 2) {
            System.out.println("[application config] [env] [port]");
            System.exit(-1);
        }

        String config = arguments[0];
        String environment = arguments[1];
        int port = arguments.length == 2 ? 8080 : Integer.parseInt(arguments[2]);

        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ApplicationConfiguration configuration = mapper.readValue(new File("./" + config + ".yml"), ApplicationConfiguration.class);
        new Launcher(config, configuration, environment, port).launch(true);
    }
}
