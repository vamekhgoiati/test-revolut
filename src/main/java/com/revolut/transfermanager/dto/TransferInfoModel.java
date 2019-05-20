package com.revolut.transfermanager.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TransferInfoModel {

    @NotNull
    @Size(min = 1)
    private String accountFrom;

    @NotNull
    @Size(min = 1)
    private String accountTo;

    @NotNull
    @Min(1L)
    private Long amount;

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}
