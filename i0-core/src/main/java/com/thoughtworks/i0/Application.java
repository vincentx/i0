package com.thoughtworks.i0;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

import java.util.Set;

public abstract class Application {
    private final String name;
    private final ImmutableSet.Builder<String> servlets = new ImmutableSet.Builder<>();
    private final ImmutableSet.Builder<String> apis = new ImmutableSet.Builder<>();
    private int port = 8080;

    protected Application(String name) {
        this.name = name;
    }

    protected void port(int port) {
        this.port = port;
    }

    protected void servlets(String... packages) {
        this.servlets.add(packages);
    }

    protected void api(String... packages) {
        this.apis.add(packages);
    }

    protected void root(String packageName) {
        api(packageName + ".api");
        servlets(packageName + ".servlets");
    }

    public String name() {
        return name;
    }

    public int port() {
        return port;
    }

    public Set<String> servlets() {
        return servlets.build();
    }

    public Set<String> api() {
        return apis.build();
    }

}
