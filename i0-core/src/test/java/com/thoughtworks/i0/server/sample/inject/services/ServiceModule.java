package com.thoughtworks.i0.server.sample.inject.services;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MessageProvider.class).toInstance(new MessageProvider() {
            @Override
            public String message() {
                return "injected";
            }
        });
    }
}
