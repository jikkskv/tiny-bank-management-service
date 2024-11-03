package com.tinybank.management.service.impl;

import com.tinybank.management.account.Account;
import com.tinybank.management.account.AccountStatus;
import com.tinybank.management.exception.DepositOperationException;
import com.tinybank.management.exception.InvalidAccountException;
import com.tinybank.management.exception.TransferOperationException;
import com.tinybank.management.exception.WithdrawOperationException;
import com.tinybank.management.service.AccountStorageDB;
import com.tinybank.management.service.AccountTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountTransactionalService")
public class AccountTransactionalServiceImpl implements AccountTransactionalService {

    private static final int REVERT_ATTEMPTS = 5;
    @Autowired
    private AccountStorageDB accountStorageDB;

    @Override
    public void deposit(Long accountId, double amount) throws DepositOperationException {
        try {
            Account account = accountStorageDB.getAccount(accountId);
            boolean addStatus = account.addBalance(amount);
            if (!addStatus) {
                throw new DepositOperationException();
            }
        } catch (RuntimeException ex) {
            throw new DepositOperationException(ex.getMessage());
        } catch (Exception ex) {
            throw new DepositOperationException();
        }
    }

    @Override
    public void withdraw(Long accountId, double amount) throws WithdrawOperationException {
        try {
            Account account = accountStorageDB.getAccount(accountId);
            boolean subtractStatus = account.subtractBalance(amount);
            if (!subtractStatus) {
                throw new WithdrawOperationException();
            }
        } catch (RuntimeException ex) {
            throw new WithdrawOperationException(ex.getMessage());
        } catch (Exception ex) {
            throw new WithdrawOperationException();
        }
    }

    @Override
    public void transfer(Long fromAccountId, Long toAccountId, double amount) throws InvalidAccountException, TransferOperationException {
        try {
            Account fromAccount = accountStorageDB.getAccount(fromAccountId);
            Account toAccount = accountStorageDB.getAccount(toAccountId);
            boolean subtractStatus = fromAccount.subtractBalance(amount);
            if (subtractStatus) {
                boolean addStatus = toAccount.addBalance(amount);
                if (!addStatus) {
                    revertWithdrawalOperation(fromAccount, amount);
                    throw new TransferOperationException();
                }
            } else {
                throw new TransferOperationException();
            }
        } catch (RuntimeException | InvalidAccountException ex) {
            throw new TransferOperationException(ex.getMessage());
        } catch (Exception ex) {
            throw new TransferOperationException();
        }
    }

    private void revertWithdrawalOperation(Account fromAccount, double amount) {
        boolean revertStatus = false;
        int maxRevertAttempts = REVERT_ATTEMPTS;
        while (!revertStatus && maxRevertAttempts >= 0) {
            revertStatus = fromAccount.addBalance(amount);
            maxRevertAttempts--;
        }
        if (!revertStatus) {
            fromAccount.setAccountStatus(AccountStatus.LOCKED);
        }
    }
}
