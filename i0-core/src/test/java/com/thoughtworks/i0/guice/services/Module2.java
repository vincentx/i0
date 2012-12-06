package com.thoughtworks.i0.guice.services;

import com.google.inject.AbstractModule;

public class Module2 extends AbstractModule {
    @Override
    protected void configure() {
        bind(Service2.class);
    }
}
