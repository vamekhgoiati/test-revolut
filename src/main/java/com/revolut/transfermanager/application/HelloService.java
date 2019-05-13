package com.revolut.transfermanager.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class HelloService {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello() {
        return Response.ok("Hello Gela", MediaType.TEXT_PLAIN).build();
    }
}
