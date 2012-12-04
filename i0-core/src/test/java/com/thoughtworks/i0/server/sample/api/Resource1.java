package com.thoughtworks.i0.server.sample.api;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Singleton
@Path("/resource1")
public class Resource1 {

    @GET
    @Path("/message")
    @Produces("text/plain")
    public String message() {
        return "message";
    }
}
