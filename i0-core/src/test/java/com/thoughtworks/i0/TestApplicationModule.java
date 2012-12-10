package com.thoughtworks.i0;

import com.thoughtworks.i0.server.sample.inject.services.MessageProvider;

@Application(name = "auto-scan",
        root = "com.thoughtworks.i0.server.sample.inject")
public class TestApplicationModule extends ApplicationModule {
    @Override
    protected void configService() {
        bind(MessageProvider.class).toInstance(new MessageProvider() {
            @Override
            public String message() {
                return "auto-scan";
            }
        });
    }
}
