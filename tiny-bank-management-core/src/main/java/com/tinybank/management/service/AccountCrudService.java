package com.tinybank.management.service;

import com.tinybank.management.account.Account;
import com.tinybank.management.exception.CancelAccountException;
import com.tinybank.management.exception.CreateAccountException;

public interface AccountCrudService {

    Account createAccount(Account account) throws CreateAccountException;

    boolean cancelAccount(Long accountId) throws CancelAccountException;
}
