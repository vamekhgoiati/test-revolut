package com.revolut.transfermanager.controller;

import com.revolut.transfermanager.dto.AccountModel;
import com.revolut.transfermanager.service.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/account")
public class AccountController {

    private final AccountService accountService;

    @Inject
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAccounts() {
        return Response.ok(accountService.getAccounts()).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAccount(@PathParam("id") String id) {
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAccount(AccountModel model) {
        return Response.ok().build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("id") String id, AccountModel model) {
        return Response.ok().build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteAccount(@PathParam("id") String id) {
        return Response.ok().build();
    }
}
