package com.tinybank.management.service.impl;

import com.tinybank.management.account.Account;
import com.tinybank.management.account.AccountStatus;
import com.tinybank.management.exception.DepositOperationException;
import com.tinybank.management.exception.InvalidAccountException;
import com.tinybank.management.exception.TransferOperationException;
import com.tinybank.management.exception.WithdrawOperationException;
import com.tinybank.management.service.AccountStorageDB;
import com.tinybank.management.service.AccountTransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("accountTransactionalService")
@Slf4j
public class AccountTransactionalServiceImpl implements AccountTransactionalService {

    private static final int REVERT_ATTEMPTS = 5;
    @Autowired
    private AccountStorageDB accountStorageDB;

    @Override
    public void deposit(Long accountId, double amount, String remarks) throws DepositOperationException {
        log.info("Start of deposit, accountId: {}, amount: {}, remarks: {}", accountId, amount, remarks);
        try {
            Account account = accountStorageDB.getAccount(accountId);
            boolean addStatus = account.addBalance(amount, remarks);
            log.info("End of deposit, accountId: {}, amount: {}, remarks: {}, addStatus: {}", accountId, amount, remarks, addStatus);
            if (!addStatus) {
                throw new DepositOperationException();
            }
        } catch (RuntimeException | DepositOperationException ex) {
            log.error("Error in deposit, accountId: {}, amount: {}, remarks: {}", accountId, amount, remarks, ex);
            throw new DepositOperationException(ex.getMessage());
        } catch (Exception ex) {
            log.error("Error in deposit, accountId: {}, amount: {}, remarks: {}", accountId, amount, remarks, ex);
            throw new DepositOperationException();
        }
    }

    @Override
    public void withdraw(Long accountId, double amount, String remarks) throws WithdrawOperationException {
        log.info("Start of withdraw, accountId: {}, amount: {}, remarks: {}", accountId, amount, remarks);
        try {
            Account account = accountStorageDB.getAccount(accountId);
            boolean subtractStatus = account.subtractBalance(amount, remarks);
            log.info("End of withdraw, accountId: {}, amount: {}, remarks: {}, subtractStatus: {}", accountId, amount, remarks, subtractStatus);
            if (!subtractStatus) {
                throw new WithdrawOperationException();
            }
        } catch (RuntimeException | WithdrawOperationException ex) {
            log.error("Error in withdraw, accountId: {}, amount: {}, remarks: {}", accountId, amount, remarks, ex);
            throw new WithdrawOperationException(ex.getMessage());
        } catch (Exception ex) {
            log.error("Error in withdraw, accountId: {}, amount: {}, remarks: {}", accountId, amount, remarks, ex);
            throw new WithdrawOperationException();
        }
    }

    @Override
    public void transfer(Long fromAccountId, Long toAccountId, double amount, String remarks) throws InvalidAccountException, TransferOperationException {
        log.info("Start of transfer, fromAccountId: {}, toAccountId: {}, amount: {}, remarks: {}", fromAccountId, toAccountId, amount, remarks);
        try {
            Account fromAccount = accountStorageDB.getAccount(fromAccountId);
            Account toAccount = accountStorageDB.getAccount(toAccountId);
            boolean subtractStatus = fromAccount.subtractBalance(amount, remarks);
            log.info("transfer operation, fromAccountId: {}, toAccountId: {}, amount: {}, remarks: {}, subtractStatus: {}", fromAccountId, toAccountId, amount, remarks, subtractStatus);
            if (subtractStatus) {
                boolean addStatus = toAccount.addBalance(amount, remarks);
                log.info("End of transfer, fromAccountId: {}, toAccountId: {}, amount: {}, remarks: {}, addStatus: {}", fromAccountId, toAccountId, amount, remarks, addStatus);
                if (!addStatus) {
                    log.info("revertWithdrawalOperation, fromAccountId: {}, toAccountId: {}, amount: {}, remarks: {}, addStatus: {}", fromAccountId, toAccountId, amount, remarks, addStatus);
                    revertWithdrawalOperation(fromAccount, amount, remarks);
                    throw new TransferOperationException();
                }
            } else {
                throw new TransferOperationException();
            }
        } catch (TransferOperationException | RuntimeException | InvalidAccountException ex) {
            log.error("Error in transfer, fromAccountId: {}, toAccountId: {}, amount: {}, remarks: {}", fromAccountId, toAccountId, amount, remarks, ex);
            throw new TransferOperationException(ex.getMessage());
        } catch (Exception ex) {
            log.error("Error in transfer, fromAccountId: {}, toAccountId: {}, amount: {}, remarks: {}", fromAccountId, toAccountId, amount, remarks, ex);
            throw new TransferOperationException();
        }
    }

    private void revertWithdrawalOperation(Account fromAccount, double amount, String remarks) {
        boolean revertStatus = false;
        int maxRevertAttempts = REVERT_ATTEMPTS;
        while (!revertStatus && maxRevertAttempts >= 0) {
            revertStatus = fromAccount.addBalance(amount, remarks);
            log.info("revertWithdrawalOperation, fromAccountId: {}, amount: {}, remarks: {}, addStatus: {}", fromAccount, amount, remarks, remarks);
            maxRevertAttempts--;
        }
        if (!revertStatus) {
            fromAccount.setAccountStatus(AccountStatus.LOCKED);
        }
    }
}
