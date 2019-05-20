package com.revolut.transfermanager.service;

import com.revolut.transfermanager.dao.AccountDao;
import com.revolut.transfermanager.dto.AccountModel;
import com.revolut.transfermanager.model.Account;
import com.revolut.transfermanager.service.impl.AccountServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class AccountServiceTest {

    private AccountService accountService;

    @Before
    public void init() {
        accountService = new AccountServiceImpl(new AccountDaoMock());
    }

    @Test
    public void testAccountCreation() {
        AccountModel accountModel = new AccountModel();
        accountModel.setName("Test");
        accountModel.setBalance(100L);
        AccountModel createdAccount = accountService.saveAccount(accountModel);

        Assert.assertNotNull(createdAccount);
        Assert.assertNotNull(createdAccount.getId());
        Assert.assertEquals(accountModel.getName(), createdAccount.getName());
        Assert.assertEquals(accountModel.getBalance(), createdAccount.getBalance());
        Assert.assertEquals(Long.valueOf(1L), createdAccount.getVersion());
        Assert.assertEquals(1, accountService.getAccounts().size());

        AccountModel accountById = accountService.getById(createdAccount.getId());
        Assert.assertNotNull(accountById);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullAccountThrowsException() {
        accountService.saveAccount(null);
    }

    @Test
    public void testAccountUpdate() {
        AccountModel accountModel = new AccountModel();
        accountModel.setName("Test");
        accountModel.setBalance(100L);
        AccountModel createdAccount = accountService.saveAccount(accountModel);

        Assert.assertNotNull(createdAccount);
        Assert.assertEquals(1, accountService.getAccounts().size());
        String id = createdAccount.getId();

        createdAccount.setName("Test2");
        createdAccount.setBalance(200L);
        accountService.saveAccount(createdAccount);
        Assert.assertEquals(1, accountService.getAccounts().size());

        AccountModel updatedAccount = accountService.getById(id);
        Assert.assertNotNull(updatedAccount);
        Assert.assertEquals("Test2", updatedAccount.getName());
        Assert.assertEquals(Long.valueOf(200L), updatedAccount.getBalance());
        Assert.assertEquals(Long.valueOf(2L), updatedAccount.getVersion());
    }

    @Test
    public void testAccountDeletion() {
        AccountModel accountModel = new AccountModel();
        accountModel.setName("Test");
        accountModel.setBalance(100L);
        AccountModel createdAccount = accountService.saveAccount(accountModel);

        Assert.assertNotNull(createdAccount);
        String id = createdAccount.getId();

        Assert.assertNotNull(createdAccount.getId());
        Assert.assertEquals(accountModel.getName(), createdAccount.getName());
        Assert.assertEquals(accountModel.getBalance(), createdAccount.getBalance());
        Assert.assertEquals(Long.valueOf(1L), createdAccount.getVersion());
        Assert.assertEquals(1, accountService.getAccounts().size());

        accountService.deleteAccount(id);
        Assert.assertNull(accountService.getById(id));
        Assert.assertEquals(0, accountService.getAccounts().size());
    }

    @Test
    public void testGetAccountsById() {
        AccountModel accountModel1 = new AccountModel();
        accountModel1.setName("Test");
        accountModel1.setBalance(100L);
        AccountModel createdAccount1 = accountService.saveAccount(accountModel1);
        Assert.assertNotNull(createdAccount1);
        String id1 = createdAccount1.getId();

        AccountModel accountModel2 = new AccountModel();
        accountModel2.setName("Test2");
        accountModel2.setBalance(200L);
        AccountModel createdAccount2 = accountService.saveAccount(accountModel2);
        Assert.assertNotNull(createdAccount2);
        String id2 = createdAccount2.getId();

        List<AccountModel> accounts = accountService.getByIds(Arrays.asList(id1, id2));
        Assert.assertNotNull(accounts);
        Assert.assertEquals(2, accounts.size());
    }

    @Test
    public void testGetAllAccounts() {
        AccountModel accountModel1 = new AccountModel();
        accountModel1.setName("Test");
        accountModel1.setBalance(100L);
        accountService.saveAccount(accountModel1);

        AccountModel accountModel2 = new AccountModel();
        accountModel2.setName("Test2");
        accountModel2.setBalance(200L);
        accountService.saveAccount(accountModel2);

        AccountModel accountModel3 = new AccountModel();
        accountModel3.setName("Test3");
        accountModel3.setBalance(300L);
        accountService.saveAccount(accountModel3);

        List<AccountModel> accounts = accountService.getAccounts();
        Assert.assertNotNull(accounts);
        Assert.assertEquals(3, accounts.size());
    }

    class AccountDaoMock implements AccountDao {

        Map<String, Account> accounts = new HashMap<>();

        @Override
        public Account getById(String id) {
            return accounts.get(id);
        }

        @Override
        public List<Account> getByIds(List<String> ids) {
            return ids.stream().map(accounts::get).filter(Objects::nonNull).collect(Collectors.toList());
        }

        @Override
        public List<Account> getAll() {
            return new ArrayList<>(accounts.values());
        }

        @Override
        public Account saveAccount(Account account) {
            if (account.getId() == null) {
                account.setId(UUID.randomUUID().toString());
                account.setVersion(1L);
            } else {
                account.setVersion(account.getVersion() + 1);
            }
            accounts.put(account.getId(), account);
            return accounts.get(account.getId());
        }

        @Override
        public void deleteAccount(String id) {
            accounts.remove(id);
        }

        @Override
        public boolean makeTransfer(Account accountFrom, Account accountTo, Long amount) {
            accountFrom.setBalance(accountFrom.getBalance() - amount);
            accountFrom.setVersion(accountFrom.getVersion() + 1);
            accountTo.setBalance(accountTo.getBalance() + amount);
            accountTo.setVersion(accountTo.getVersion() + 1);
            return true;
        }
    }
}
