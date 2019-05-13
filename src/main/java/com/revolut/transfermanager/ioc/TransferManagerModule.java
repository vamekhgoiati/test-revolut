package com.revolut.transfermanager.ioc;

import com.google.inject.AbstractModule;
import com.revolut.transfermanager.controller.AccountController;
import com.revolut.transfermanager.controller.TransferController;
import com.revolut.transfermanager.dao.AccountDao;
import com.revolut.transfermanager.dao.impl.InMemoryAccountDao;
import com.revolut.transfermanager.service.AccountService;
import com.revolut.transfermanager.service.TransferService;
import com.revolut.transfermanager.service.impl.AccountServiceImpl;
import com.revolut.transfermanager.service.impl.TransferServiceImpl;

public class TransferManagerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountController.class);
        bind(TransferController.class);
        bind(AccountDao.class).to(InMemoryAccountDao.class);
        bind(TransferService.class).to(TransferServiceImpl.class);
        bind(AccountService.class).to(AccountServiceImpl.class);
    }
}
