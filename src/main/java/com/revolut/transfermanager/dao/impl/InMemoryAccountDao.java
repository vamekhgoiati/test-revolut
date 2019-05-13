package com.revolut.transfermanager.dao.impl;

import com.revolut.transfermanager.dao.AccountDao;
import com.revolut.transfermanager.model.Account;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class InMemoryAccountDao implements AccountDao {

    private static final ConcurrentMap<String, Account> accountMap = new ConcurrentHashMap<>();

    static {
        accountMap.put("1", new Account("1", "Gela", 100L));
        accountMap.put("2", new Account("2", "Gocha", 200L));
        accountMap.put("3", new Account("3", "Grisha", 300L));
        accountMap.put("4", new Account("4", "Gosha", 400L));
    }
    @Override
    public Account getById(String id) {
        return accountMap.get(id);
    }

    @Override
    public List<Account> getByIds(List<String> ids) {
        Set<String> idSet = new HashSet<>(ids);
        return accountMap.entrySet().stream()
                .filter(e -> idSet.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> getAll() {
        return new ArrayList<>(accountMap.values());
    }

    @Override
    public Account saveAccount(Account account) {
        if (account.getId() == null) {
            account.setId(UUID.randomUUID().toString());
            account.setVersion(1L);

            accountMap.put(account.getId(), account);
        } else {
            accountMap.computeIfPresent(account.getId(), (k, v) -> {
                if (v.getVersion() != account.getVersion()) {
                    throw new ConcurrentModificationException("Account versions does not match");
                }
                account.setVersion(account.getVersion() + 1);
                return account;
            });
        }
        return accountMap.get(account.getId());
    }
}
