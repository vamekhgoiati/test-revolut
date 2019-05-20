package com.revolut.transfermanager.dto;

import com.revolut.transfermanager.model.Account;

public class AccountModel {

    private String id;
    private String name;
    private Long balance;
    private Long version;

    public AccountModel() {
    }

    public AccountModel(Account acc) {
        if (acc != null) {
            this.id = acc.getId();
            this.name = acc.getName();
            this.balance = acc.getBalance();
            this.version = acc.getVersion();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
