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
        AccountModel model = accountService.getById(id);
        if (model == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.ok(model).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveAccount(AccountModel model) {
        return Response.ok(accountService.saveAccount(model)).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAccount(@PathParam("id") String id, AccountModel model) {
        if (accountService.getById(id) == null) {
            throw new NotFoundException(String.format("Entity with id %s does not exist", id));
        }
        model.setId(id);
        return Response.ok(accountService.saveAccount(model)).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteAccount(@PathParam("id") String id) {
        if (accountService.getById(id) == null) {
            throw new NotFoundException(String.format("Entity with id %s does not exist", id));
        }
        accountService.deleteAccount(id);

        return Response.ok().build();
    }
}
