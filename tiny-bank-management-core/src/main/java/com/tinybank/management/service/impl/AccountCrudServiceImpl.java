package com.tinybank.management.service.impl;

import com.tinybank.management.account.Account;
import com.tinybank.management.account.Transaction;
import com.tinybank.management.exception.CancelAccountException;
import com.tinybank.management.exception.CreateAccountException;
import com.tinybank.management.exception.InvalidAccountException;
import com.tinybank.management.service.AccountCrudService;
import com.tinybank.management.service.AccountStorageDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service("accountCrudService")
@Slf4j
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
            log.info("Start of validateAccountInfo, account: {}", account);
            boolean validationStatus = validateAccountInfo(account);
            log.info("End of validateAccountInfo, account: {}, validationStatus: {}", account, validationStatus);
            if (validationStatus) {
                Long generateAccountId = accountIdIncrementer.incrementAndGet();
                account.setAccountId(generateAccountId);
                log.info("Start of saveAccount, account: {}", account);
                Account createdAccount = saveAccount(account);
                log.info("End of saveAccount, account: {}", account);
                return createdAccount;
            } else {
                log.error("Error in createAccount, account: {}, invalid input", account);
                throw new CreateAccountException("Invalid input for account creation");
            }
        } catch (CreateAccountException ex) {
            log.error("Error in createAccount, account: {}, CreateAccountException", account, ex);
            throw new CreateAccountException(ex.getMessage());
        } catch (Exception ex) {
            log.error("Error in createAccount, account: {}, Exception", account, ex);
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
            log.info("cancelAccount request, accountId: {}", accountId);
            if (Objects.nonNull(accountId)) {
                return accountStorageDB.deleteAccount(accountId);
            }
        } catch (Exception ex) {
            log.info("Error in cancelAccount, accountId: {}, Exception:", accountId, ex);
            throw new CancelAccountException();
        }
        return false;
    }

    @Override
    public List<Transaction> getTransaction(Long accountId) throws InvalidAccountException {
        if (Objects.nonNull(accountId)) {
            Account account = accountStorageDB.getAccount(accountId);
            return account.getTransactions();
        }
        return List.of();
    }

    @Override
    public Double getBalance(Long accountId) throws InvalidAccountException {
        if (Objects.nonNull(accountId)) {
            Account account = accountStorageDB.getAccount(accountId);
            return account.getBalance();
        }
        return null;
    }

    private boolean validateAccountInfo(Account account) {
        return Objects.nonNull(account) && Objects.nonNull(account.getUser());
    }
}
