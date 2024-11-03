package com.tinybank.management.service.impl;

import com.tinybank.management.account.Account;
import com.tinybank.management.exception.CancelAccountException;
import com.tinybank.management.exception.CreateAccountException;
import com.tinybank.management.service.AccountCrudService;
import com.tinybank.management.service.AccountStorageDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service("accountCrudService")
public class AccountCrudServiceImpl implements AccountCrudService {

    private final AtomicLong accountIdIncrementer;

    @Autowired
    private AccountStorageDB accountStorageDB;

    public AccountCrudServiceImpl() {
        accountIdIncrementer = new AtomicLong();
    }

    @Override
    public Account createAccount(Account account) throws CreateAccountException {
        try {
            boolean validationStatus = validateAccountInfo(account);
            if (validationStatus) {
                Long generateAccountId = accountIdIncrementer.incrementAndGet();
                account.setAccountId(generateAccountId);
                Account createdAccount = saveAccount(account);
                return createdAccount;
            } else {
                throw new CreateAccountException("Invalid input for account creation");
            }
        } catch (Exception ex) {
            throw new CreateAccountException();
        }
    }

    private Account saveAccount(Account account) throws CreateAccountException {
        Account createdAccount = accountStorageDB.addAccount(account);
        if (Objects.isNull(createdAccount)) {
            throw new CreateAccountException("Account storage failed");
        }
        return createdAccount;
    }

    @Override
    public boolean cancelAccount(Long accountId) throws CancelAccountException {
        try {
            if (Objects.nonNull(accountId)) {
                return accountStorageDB.deleteAccount(accountId);
            }
        } catch (Exception ex) {
            throw new CancelAccountException();
        }
        return false;
    }

    private boolean validateAccountInfo(Account account) {
        return Objects.nonNull(account) && Objects.nonNull(account.getUser());
    }
}
