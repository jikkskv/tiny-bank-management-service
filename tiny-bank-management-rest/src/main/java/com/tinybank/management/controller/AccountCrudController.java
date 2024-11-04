package com.tinybank.management.controller;

import com.tinybank.management.account.Account;
import com.tinybank.management.account.Transaction;
import com.tinybank.management.exception.BizErrorCodeEnum;
import com.tinybank.management.exception.CancelAccountException;
import com.tinybank.management.exception.CreateAccountException;
import com.tinybank.management.exception.InvalidAccountException;
import com.tinybank.management.model.account.CreateAccountRequestModel;
import com.tinybank.management.model.account.CreateAccountResponseModel;
import com.tinybank.management.service.AccountCrudService;
import com.tinybank.management.user.Role;
import com.tinybank.management.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class AccountCrudController {

    @Autowired
    private AccountCrudService accountCrudService;

    @PostMapping("/createAccount")
    public ResponseResult createAccount(@RequestBody CreateAccountRequestModel createAccountRequestModel) {
        log.info("Start of the create account: {}", createAccountRequestModel);
        try {
            User user = getUserFromRequestModel(createAccountRequestModel);
            Account account = new Account();
            account.setUser(user);
            Account createdAccount = accountCrudService.createAccount(account);
            if (Objects.nonNull(createdAccount)) {
                log.info("End of the create account, createAccountRequestModel: {} success, accountId: {}", createAccountRequestModel, createdAccount.getAccountId());
                return ResponseResult.success(new CreateAccountResponseModel(createdAccount.getAccountId()));
            }
        } catch (CreateAccountException ex) {
            log.error("End of the create account, failed with CreateAccountException", ex);
            return ResponseResult.failure(BizErrorCodeEnum.CREATE_ACCOUNT_FAILED, ex.getMessage());
        } catch (Exception ex) {
            log.error("End of the create account, failed with Exception", ex);
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
        log.info("End of the create account, failed");
        return ResponseResult.failure(BizErrorCodeEnum.CREATE_ACCOUNT_FAILED);
    }

    @PostMapping("/cancelAccount")
    public ResponseResult cancelAccount(@RequestParam(value = "accountId") Long accountId) {
        log.info("Start of the cancel accountId: {}", accountId);
        try {
            boolean cancelStatus = accountCrudService.cancelAccount(accountId);
            if (cancelStatus) {
                log.info("End of the cancel account, accountId: {}, success", accountId);
                return ResponseResult.success();
            }
        } catch (CancelAccountException ex) {
            log.error("End of the cancel account, accountId: {}, failed with CancelAccountException", accountId, ex);
            return ResponseResult.failure(BizErrorCodeEnum.CANCEL_ACCOUNT_FAILED, ex.getMessage());
        } catch (Exception ex) {
            log.error("End of the cancel account, accountId: {}, failed with Exception", accountId, ex);
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
        log.info("End of the cancel account, accountId: {}, failed", accountId);
        return ResponseResult.failure(BizErrorCodeEnum.CANCEL_ACCOUNT_FAILED);
    }

    @GetMapping("/getTransaction")
    public ResponseResult getTransaction(@RequestParam(value = "accountId") Long accountId) {
        log.debug("Start of the getTransaction accountId: {}", accountId);
        try {
            List<Transaction> transactions = accountCrudService.getTransaction(accountId);
            log.debug("End of the getTransaction, accountId: {}, transactions: {}", accountId, transactions);
            return ResponseResult.success(transactions);
        } catch (InvalidAccountException ex) {
            log.error("End of the getTransaction, accountId: {} , failed with InvalidAccountException", accountId, ex);
            return ResponseResult.failure(BizErrorCodeEnum.GET_TRANSACTION_FAILED, ex.getMessage());
        } catch (Exception ex) {
            log.error("End of the getTransaction, accountId: {}, failed with Exception", accountId, ex);
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    @GetMapping("/getBalance")
    public ResponseResult getBalance(@RequestParam(value = "accountId") Long accountId) {
        log.debug("Start of the getBalance accountId: {}", accountId);
        try {
            Double balance = accountCrudService.getBalance(accountId);
            if (Objects.nonNull(balance)) {
                log.debug("End of the getBalance, accountId: {}, balance: {}", accountId, balance);
                return ResponseResult.success(Map.of("accountBalance", balance));
            }
        } catch (InvalidAccountException ex) {
            log.error("End of the getBalance, accountId: {} , failed with InvalidAccountException", accountId, ex);
            return ResponseResult.failure(BizErrorCodeEnum.GET_TRANSACTION_FAILED, ex.getMessage());
        } catch (Exception ex) {
            log.error("End of the getBalance, accountId: {}, failed with Exception", accountId, ex);
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
        return ResponseResult.failure(BizErrorCodeEnum.GET_TRANSACTION_FAILED);
    }

    private static User getUserFromRequestModel(CreateAccountRequestModel createAccountRequestModel) throws CreateAccountException {
        Role role;
        try {
            role = Role.of(createAccountRequestModel.role());
        } catch (IllegalArgumentException ex) {
            throw new CreateAccountException("Invalid role code, Please use 'user' / 'admin' for role");
        }
        User user = User.builder()
                .name(createAccountRequestModel.name())
                .userName(createAccountRequestModel.userName())
                .password(createAccountRequestModel.password())
                .role(role)
                .build();
        return user;
    }
}
