package com.tinybank.management.controller;

import com.tinybank.management.exception.BizErrorCodeEnum;
import com.tinybank.management.exception.DepositOperationException;
import com.tinybank.management.exception.TransferOperationException;
import com.tinybank.management.exception.WithdrawOperationException;
import com.tinybank.management.model.transaction.DualAccountTransactionRequestModel;
import com.tinybank.management.model.transaction.SingleAccountTransactionRequestModel;
import com.tinybank.management.service.AccountTransactionalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AccountTransactionController {

    @Autowired
    private AccountTransactionalService accountTransactionalService;

    @PostMapping("/depositMoney")
    public ResponseResult depositMoney(@RequestBody SingleAccountTransactionRequestModel accountTransactionRequestModel) {
        try {
            accountTransactionalService.deposit(accountTransactionRequestModel.accountId(), accountTransactionRequestModel.amount());
        } catch (DepositOperationException ex) {
            return ResponseResult.failure(BizErrorCodeEnum.DEPOSIT_OPERATION_FAILED, ex.getMessage());
        } catch (Exception ex) {
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
        return ResponseResult.failure(BizErrorCodeEnum.DEPOSIT_OPERATION_FAILED);
    }

    @PostMapping("/withdrawMoney")
    public ResponseResult withdrawMoney(@RequestBody SingleAccountTransactionRequestModel accountTransactionRequestModel) {
        try {
            accountTransactionalService.withdraw(accountTransactionRequestModel.accountId(), accountTransactionRequestModel.amount());
        } catch (WithdrawOperationException ex) {
            return ResponseResult.failure(BizErrorCodeEnum.WITHDRAW_OPERATION_FAILED, ex.getMessage());
        } catch (Exception ex) {
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
        return ResponseResult.failure(BizErrorCodeEnum.WITHDRAW_OPERATION_FAILED);
    }

    @PostMapping("/transferMoney")
    public ResponseResult transferMoney(@RequestBody DualAccountTransactionRequestModel accountTransactionRequestModel) {
        try {
            accountTransactionalService.transfer(accountTransactionRequestModel.fromAccountId(), accountTransactionRequestModel.toAccountId(), accountTransactionRequestModel.amount());
        } catch (TransferOperationException ex) {
            return ResponseResult.failure(BizErrorCodeEnum.TRANSFER_OPERATION_FAILED, ex.getMessage());
        } catch (Exception ex) {
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
        return ResponseResult.failure(BizErrorCodeEnum.TRANSFER_OPERATION_FAILED);
    }

}
