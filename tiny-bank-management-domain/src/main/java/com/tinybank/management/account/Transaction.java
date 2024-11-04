package com.tinybank.management.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {
    private Long accountId;
    private Double amount;
    private TransactionType transactionType;
    private LocalDateTime transactionDate;
}
