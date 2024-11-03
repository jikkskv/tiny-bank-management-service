package com.tinybank.management.service;

import com.tinybank.management.account.Account;
import com.tinybank.management.exception.InvalidAccountException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountStorageDB {

    private final Map<Long, Account> accountStorage;

    public AccountStorageDB() {
        accountStorage = new ConcurrentHashMap<>();
    }

    public Account getAccount(Long accountId) throws InvalidAccountException {
        if (Objects.nonNull(accountId) && accountStorage.containsKey(accountId)) {
            return accountStorage.get(accountId);
        } else {
            throw new InvalidAccountException("Invalid Account Id");
        }
    }

    public Account addAccount(Account account) {
        if (Objects.nonNull(account) && Objects.nonNull(account.getAccountId())) {
            accountStorage.put(account.getAccountId(), account);
            return account;
        }
        return null;
    }

    public boolean deleteAccount(Long accountId) {
        if (Objects.nonNull(accountId) && accountStorage.containsKey(accountId)) {
            accountStorage.remove(accountId);
            return true;
        }
        return false;
    }
}
