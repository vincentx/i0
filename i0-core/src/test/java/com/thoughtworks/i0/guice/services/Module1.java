package com.thoughtworks.i0.guice.services;

import com.google.inject.AbstractModule;

public class Module1 extends AbstractModule {
    @Override
    protected void configure() {
        bind(Service1.class);
    }
}
