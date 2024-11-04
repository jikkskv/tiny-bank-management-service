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
        log.info("Start of the depositMoney, accountTransactionRequestModel: {}", accountTransactionRequestModel);
        try {
            accountTransactionalService.deposit(accountTransactionRequestModel.accountId(), accountTransactionRequestModel.amount(), accountTransactionRequestModel.remarks());
            log.info("End of the depositMoney, accountTransactionRequestModel: {}, success", accountTransactionRequestModel);
            return ResponseResult.success();
        } catch (DepositOperationException ex) {
            log.error("End of the depositMoney, accountTransactionRequestModel: {}, failed", accountTransactionRequestModel, ex);
            return ResponseResult.failure(BizErrorCodeEnum.DEPOSIT_OPERATION_FAILED, ex.getMessage());
        } catch (Exception ex) {
            log.error("End of the depositMoney, accountTransactionRequestModel: {}, failed", accountTransactionRequestModel, ex);
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    @PostMapping("/withdrawMoney")
    public ResponseResult withdrawMoney(@RequestBody SingleAccountTransactionRequestModel accountTransactionRequestModel) {
        log.info("Start of the withdrawMoney, accountTransactionRequestModel: {}", accountTransactionRequestModel);
        try {
            accountTransactionalService.withdraw(accountTransactionRequestModel.accountId(), accountTransactionRequestModel.amount(), accountTransactionRequestModel.remarks());
            log.info("End of the withdrawMoney, accountTransactionRequestModel: {}, success", accountTransactionRequestModel);
            return ResponseResult.success();
        } catch (WithdrawOperationException ex) {
            log.error("End of the withdrawMoney, accountTransactionRequestModel: {}, failed", accountTransactionRequestModel, ex);
            return ResponseResult.failure(BizErrorCodeEnum.WITHDRAW_OPERATION_FAILED, ex.getMessage());
        } catch (Exception ex) {
            log.error("End of the withdrawMoney, accountTransactionRequestModel: {}, failed", accountTransactionRequestModel, ex);
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
    }

    @PostMapping("/transferMoney")
    public ResponseResult transferMoney(@RequestBody DualAccountTransactionRequestModel accountTransactionRequestModel) {
        log.info("Start of the transferMoney, accountTransactionRequestModel: {}", accountTransactionRequestModel);
        try {
            accountTransactionalService.transfer(accountTransactionRequestModel.fromAccountId(), accountTransactionRequestModel.toAccountId(), accountTransactionRequestModel.amount(), accountTransactionRequestModel.remarks());
            log.info("End of the transferMoney, accountTransactionRequestModel: {}, success", accountTransactionRequestModel);
            return ResponseResult.success();
        } catch (TransferOperationException ex) {
            log.error("End of the transferMoney, accountTransactionRequestModel: {}, failed", accountTransactionRequestModel, ex);
            return ResponseResult.failure(BizErrorCodeEnum.TRANSFER_OPERATION_FAILED, ex.getMessage());
        } catch (Exception ex) {
            log.error("End of the transferMoney, accountTransactionRequestModel: {}, failed", accountTransactionRequestModel, ex);
            return ResponseResult.failure(BizErrorCodeEnum.SYSTEM_ERROR);
        }
    }

}
