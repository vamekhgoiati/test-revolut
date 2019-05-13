package com.revolut.transfermanager.service.impl;

import com.google.inject.Inject;
import com.revolut.transfermanager.dao.AccountDao;
import com.revolut.transfermanager.service.TransferService;

public class TransferServiceImpl implements TransferService {

    private final AccountDao accountDao;

    @Inject
    public TransferServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
}
