package com.revolut.transfermanager.service.impl;

import com.google.inject.Inject;
import com.revolut.transfermanager.dao.AccountDao;
import com.revolut.transfermanager.dto.AccountModel;
import com.revolut.transfermanager.service.AccountService;

import java.util.List;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;

    @Inject
    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public AccountModel getById(String id) {
        return new AccountModel();
    }

    @Override
    public List<AccountModel> getAccounts() {
        return accountDao.getAll().stream().map(AccountModel::new).collect(Collectors.toList());
    }
}
