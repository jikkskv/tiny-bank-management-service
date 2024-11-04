package com.tinybank.management.model.transaction;

public record SingleAccountTransactionRequestModel(Long accountId, double amount, String remarks) {
}
