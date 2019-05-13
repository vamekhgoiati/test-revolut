package com.revolut.transfermanager.service;

import com.revolut.transfermanager.dto.AccountModel;

import java.util.List;

public interface AccountService {

    AccountModel getById(String id);

    List<AccountModel> getAccounts();
}
