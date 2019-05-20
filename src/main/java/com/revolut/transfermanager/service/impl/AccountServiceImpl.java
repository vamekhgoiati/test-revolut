package com.revolut.transfermanager.service.impl;

import com.google.inject.Inject;
import com.revolut.transfermanager.dao.AccountDao;
import com.revolut.transfermanager.dto.AccountModel;
import com.revolut.transfermanager.dto.TransferInfoModel;
import com.revolut.transfermanager.model.Account;
import com.revolut.transfermanager.service.AccountService;

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AccountServiceImpl implements AccountService {

    private static final Logger logger = Logger.getLogger(AccountServiceImpl.class.getSimpleName());
    private static final int NUM_RETRIES = 5;

    private final AccountDao accountDao;

    @Inject
    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public AccountModel getById(String id) {
        Account account = accountDao.getById(id);
        if (account == null) {
            return null;
        }

        return new AccountModel(account);
    }

    @Override
    public List<AccountModel> getAccounts() {
        return accountDao.getAll().stream().map(AccountModel::new).collect(Collectors.toList());
    }

    @Override
    public AccountModel saveAccount(AccountModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Account model cannot be null");
        }

        Account account;
        if (model.getId() == null) {
            account = new Account();
        } else {
            account = accountDao.getById(model.getId());
        }

        if (model.getName() != null) {
            account.setName(model.getName());
        }

        if (model.getVersion() != null) {
            account.setVersion(model.getVersion());
        }

        if (model.getBalance() != null) {
            account.setBalance(model.getBalance());
        }

        return new AccountModel(accountDao.saveAccount(account));
    }

    @Override
    public void deleteAccount(String id) {
        accountDao.deleteAccount(id);
    }

    @Override
    public void makeTransfer(TransferInfoModel transferModel) {
        int retries = 0;
        boolean success = false;
        do {
            try {
                Account accountFrom = accountDao.getById(transferModel.getAccountFrom());
                Account accountTo = accountDao.getById(transferModel.getAccountTo());
                if (accountFrom != null && accountTo != null) {
                    if (accountFrom.getBalance() >= transferModel.getAmount()) {
                        success = accountDao.makeTransfer(accountFrom, accountTo, transferModel.getAmount());
                    } else {
                        throw new RuntimeException("Insufficient funds for account with id " + accountFrom.getId());
                    }
                }
            } catch (RuntimeException ex) {
                logger.log(Level.INFO, "Couldn't make transfer. retrying ...");
            }

        } while (!success && (++retries <= NUM_RETRIES));

        if (!success) {
            throw new RuntimeException("Couldn't make transfer. Please try again");
        }
    }

    @Override
    public List<AccountModel> getByIds(List<String> ids) {
        return accountDao.getByIds(ids).stream().map(AccountModel::new).collect(Collectors.toList());
    }
}
