package com.tinybank.management.service;

import com.tinybank.management.exception.DepositOperationException;
import com.tinybank.management.exception.InvalidAccountException;
import com.tinybank.management.exception.TransferOperationException;
import com.tinybank.management.exception.WithdrawOperationException;

public interface AccountTransactionalService {

    void deposit(Long accountId, double amount) throws DepositOperationException;

    void withdraw(Long accountId, double amount) throws WithdrawOperationException;

    void transfer(Long fromAccountId, Long toAccountId, double amount) throws InvalidAccountException, TransferOperationException;
}
