package com.revolut.transfermanager.dao;

import com.revolut.transfermanager.model.Account;

import java.util.List;

public interface AccountDao {

    Account getById(String id);

    List<Account> getByIds(List<String> ids);

    List<Account> getAll();

    Account saveAccount(Account account);
}
