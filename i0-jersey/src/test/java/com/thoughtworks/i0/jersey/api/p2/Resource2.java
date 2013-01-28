package com.thoughtworks.i0.jersey.api.p2;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/p2")
public class Resource2 {

    @GET
    public String message() {
        return "resource2";
    }

    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Data json() {
        return new Data("data");
    }

    @POST
    @Path("/echo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Data echo(Data data) {
        return data;
    }
}

