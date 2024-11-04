package com.tinybank.management.controller;

import com.tinybank.management.account.Account;
import com.tinybank.management.exception.BizErrorCodeEnum;
import com.tinybank.management.exception.CancelAccountException;
import com.tinybank.management.exception.CreateAccountException;
import com.tinybank.management.model.account.CreateAccountRequestModel;
import com.tinybank.management.model.account.CreateAccountResponseModel;
import com.tinybank.management.service.AccountCrudService;
import com.tinybank.management.user.Role;
import com.tinybank.management.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Slf4j
public class AccountCrudController {

    @Autowired
    private AccountCrudService accountCrudService;

    @PostMapping("/createAccount")
    public ResponseResult createAccount(@RequestBody CreateAccountRequestModel createAccountRequestModel) {
        try {
            User user = getUserFromRequestModel(createAccountRequestModel);
            Account account = new Account();
            account.setUser(user);
            Account createdAccount = accountCrudService.createAccount(account);
            if (Objects.nonNull(createdAccount)) {
                return ResponseResult.success(new CreateAccountResponseModel(createdAccount.getAccountId()));
            }
        } catch (CreateAccountException ex) {
            return ResponseResult.failure(BizErrorCodeEnum.CREATE_ACCOUNT_FAILED, ex.getMessage());
        } catch (Exception ex) {
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
        return ResponseResult.failure(BizErrorCodeEnum.CREATE_ACCOUNT_FAILED);
    }

    @PostMapping("/cancelAccount")
    public ResponseResult cancelAccount(@RequestParam(value = "accountId") Long accountId) {
        try {
            boolean cancelStatus = accountCrudService.cancelAccount(accountId);
            if (cancelStatus) {
                return ResponseResult.success();
            }
        } catch (CancelAccountException ex) {
            return ResponseResult.failure(BizErrorCodeEnum.CANCEL_ACCOUNT_FAILED, ex.getMessage());
        } catch (Exception ex) {
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
        return ResponseResult.failure(BizErrorCodeEnum.CANCEL_ACCOUNT_FAILED);
    }

    private static User getUserFromRequestModel(CreateAccountRequestModel createAccountRequestModel) {
        User user = User.builder()
                .name(createAccountRequestModel.name())
                .userName(createAccountRequestModel.userName())
                .password(createAccountRequestModel.password())
                .role(Role.of(createAccountRequestModel.role()))
                .build();
        return user;
    }
}
