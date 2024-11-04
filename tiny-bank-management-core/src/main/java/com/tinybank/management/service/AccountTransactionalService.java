package com.tinybank.management.service;

import com.tinybank.management.exception.DepositOperationException;
import com.tinybank.management.exception.InvalidAccountException;
import com.tinybank.management.exception.TransferOperationException;
import com.tinybank.management.exception.WithdrawOperationException;

public interface AccountTransactionalService {

    void deposit(Long accountId, double amount, String remarks) throws DepositOperationException;

    void withdraw(Long accountId, double amount, String remarks) throws WithdrawOperationException;

    void transfer(Long fromAccountId, Long toAccountId, double amount, String remarks) throws InvalidAccountException, TransferOperationException;
}
