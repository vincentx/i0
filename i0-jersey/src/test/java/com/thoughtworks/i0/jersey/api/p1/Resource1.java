package com.thoughtworks.i0.jersey.api.p1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/p1")
public class Resource1 {

    @GET
    public String message() {
        return "resource1";
    }
}
