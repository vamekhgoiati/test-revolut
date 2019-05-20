package com.revolut.transfermanager.service;

import com.revolut.transfermanager.dto.AccountModel;
import com.revolut.transfermanager.dto.TransferInfoModel;

import java.util.List;

public interface AccountService {

    AccountModel getById(String id);

    List<AccountModel> getAccounts();

    AccountModel saveAccount(AccountModel model);

    void deleteAccount(String id);

    void makeTransfer(TransferInfoModel transferModel);

    List<AccountModel> getByIds(List<String> ids);
}
