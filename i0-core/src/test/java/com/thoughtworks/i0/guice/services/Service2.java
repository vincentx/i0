package com.thoughtworks.i0.guice.services;

import javax.inject.Inject;

public class Service2 {
    private Service1 service1;

    @Inject
    public Service2(Service1 service1) {
        this.service1 = service1;
    }

    public String message() {
        return service1.message();
    }
}
