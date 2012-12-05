package com.thoughtworks.i0.server.sample.inject.api;

import com.thoughtworks.i0.server.sample.inject.services.MessageProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Singleton
@Path("/resource1")
public class Resource1 {
    private MessageProvider provider;

    @Inject
    public Resource1(MessageProvider provider) {
        this.provider = provider;
    }

    @GET
    @Path("/message")
    @Produces("text/plain")
    public String message() {
        return provider.message();
    }
}
