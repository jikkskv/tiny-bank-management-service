package com.tinybank.management.service;

import com.tinybank.management.account.Account;
import com.tinybank.management.account.Transaction;
import com.tinybank.management.exception.CancelAccountException;
import com.tinybank.management.exception.CreateAccountException;
import com.tinybank.management.exception.InvalidAccountException;

import java.util.List;

public interface AccountCrudService {

    Account createAccount(Account account) throws CreateAccountException;

    boolean cancelAccount(Long accountId) throws CancelAccountException;

    List<Transaction> getTransaction(Long accountId) throws InvalidAccountException;
}
