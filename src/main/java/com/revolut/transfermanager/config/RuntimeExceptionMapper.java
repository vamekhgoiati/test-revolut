package com.revolut.transfermanager.config;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionMapper extends Exception implements ExceptionMapper<RuntimeException> {
    @Override
    public Response toResponse(RuntimeException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
