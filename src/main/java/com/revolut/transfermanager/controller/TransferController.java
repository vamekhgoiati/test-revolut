package com.revolut.transfermanager.controller;

import com.google.inject.Inject;
import com.revolut.transfermanager.dto.TransferInfoModel;
import com.revolut.transfermanager.service.AccountService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transfer")
public class TransferController {

    private final AccountService accountService;

    @Inject
    public TransferController(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response transfer(TransferInfoModel transfer) {
        accountService.makeTransfer(transfer);
        return Response.ok().build();
    }
}
