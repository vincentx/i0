package com.thoughtworks.i0;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.thoughtworks.i0.server.JettyServer;

import java.util.Set;

public abstract class Application {
    private String name = "/";
    private int port = 8080;
    private String jdbcUrl;
    private String jdbcDriver;

    private final ImmutableSet.Builder<String> servlets = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<String> apis = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<String> persistenceUnits = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<Module> modules = new ImmutableSet.Builder<>();


    protected Application() {
    }

    protected Application(String name) {
        this.name = name;
    }

    protected void name(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    protected void port(int port) {
        this.port = port;
    }

    public int port() {
        return port;
    }

    protected void  servlets(String... packages) {
        this.servlets.add(packages);
    }

    public Set<String> servlets() {
        return servlets.build();
    }

    protected void api(String... packages) {
        this.apis.add(packages);
    }

    public Set<String> api() {
        return apis.build();
    }

    public void install(Module... modules) {
        this.modules.add(modules);
    }

    public Set<Module> modules() {
        return modules.build();
    }

    protected void persistence(String... units) {
        persistenceUnits.add(units);
    }

    public Set<String> persistence() {
        return persistenceUnits.build();
    }

    protected void root(String packageName) {
        api(packageName + ".api");
        servlets(packageName + ".servlets");
        persistence(packageName + ".domain");
    }

    protected void db(String url, String driver) {
        this.jdbcUrl = url;
        this.jdbcDriver = driver;
    }

    public void standalone() throws Exception {
        new JettyServer(this).start(true);
    }

    public String jdbcUrl() {
        return jdbcUrl;
    }


    public String jdbcDriver() {
        return jdbcDriver;
    }
}
