package com.tinybank.management.account;

import com.tinybank.management.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    @Getter
    @Setter
    private Long accountId;

    @Getter
    private volatile Double balance;

    @Getter
    private List<Transaction> transactions;

    @Getter
    @Setter
    private User user;

    @Setter
    private AccountStatus accountStatus;

    private final Lock lock;

    public static final int LOCK_ATTEMPT_INTERVAL = 2000;

    public Account() {
        balance = 0.0D;
        lock = new ReentrantLock();
        accountStatus = AccountStatus.AVAILABLE;
        transactions = new LinkedList<>();
    }

    public boolean addBalance(double amount, String remarks) {
        if (!accountStatus.equals(AccountStatus.AVAILABLE)) return false;
        if (amount < 0) return false;
        balance = balance + amount;
        addTransaction(this.accountId, amount, TransactionType.DEPOSIT, remarks);
        return true;
    }

    public boolean subtractBalance(double amount, String remarks) {
        if (!accountStatus.equals(AccountStatus.AVAILABLE)) return false;
        if (amount < 0) return false;
        if (amount > balance) throw new RuntimeException("Insufficient balance");
        try {
            boolean lockStatus = lock.tryLock(LOCK_ATTEMPT_INTERVAL, TimeUnit.MILLISECONDS);
            if (!lockStatus || amount > balance)
                throw new RuntimeException("Insufficient balance due to concurrent transaction");
            if (amount <= balance) {
                balance = balance - amount;
                addTransaction(this.accountId, amount, TransactionType.WITHDRAW, remarks);
                return true;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Subtract balance operation failed");
        } finally {
            lock.unlock();
        }
        return false;
    }

    private void addTransaction(long accountId, double amount, TransactionType transactionType, String remarks) {
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .amount(amount)
                .transactionType(transactionType)
                .transactionDate(LocalDateTime.now())
                .remarks(remarks)
                .build();
        this.getTransactions().add(transaction);
    }
}
