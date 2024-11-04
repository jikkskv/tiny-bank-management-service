package com.tinybank.management.model.transaction;

public record DualAccountTransactionRequestModel(Long fromAccountId, Long toAccountId, double amount) {
}
