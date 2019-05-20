package com.revolut.transfermanager.ioc;

import com.google.inject.AbstractModule;
import com.revolut.transfermanager.controller.AccountController;
import com.revolut.transfermanager.controller.TransferController;
import com.revolut.transfermanager.dao.AccountDao;
import com.revolut.transfermanager.dao.impl.AccountDaoImpl;
import com.revolut.transfermanager.service.AccountService;
import com.revolut.transfermanager.service.impl.AccountServiceImpl;

public class TransferManagerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountController.class);
        bind(TransferController.class);
        bind(AccountDao.class).to(AccountDaoImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
    }
}
