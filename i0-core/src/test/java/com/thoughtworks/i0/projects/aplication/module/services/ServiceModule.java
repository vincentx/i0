package com.thoughtworks.i0.projects.aplication.module.services;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CService.class).to(CServiceImpl.class);
    }
}
