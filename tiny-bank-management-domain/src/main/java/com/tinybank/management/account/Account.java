package com.tinybank.management.account;

import com.tinybank.management.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    @Setter
    @Getter
    private Long accountId;

    @Getter
    private volatile Double balance;

    @Getter
    private List<Transaction> transactions;

    @Getter
    private User user;

    @Setter
    private AccountStatus accountStatus;

    private final Lock lock;

    public static final int LOCK_ATTEMPT_INTERVAL = 2000;

    Account() {
        balance = 0.0D;
        lock = new ReentrantLock();
        accountStatus = AccountStatus.AVAILABLE;
    }

    public boolean addBalance(double amount) {
        if(!accountStatus.equals(AccountStatus.AVAILABLE)) return false;
        if (amount < 0) return false;
        balance = balance + amount;
        return true;
    }

    public boolean subtractBalance(double amount) {
        if(!accountStatus.equals(AccountStatus.AVAILABLE)) return false;
        if (amount < 0) return false;
        if (amount > balance) throw new RuntimeException("Insufficient balance");
        try {
            boolean lockStatus = lock.tryLock(LOCK_ATTEMPT_INTERVAL, TimeUnit.MILLISECONDS);
            if (!lockStatus || amount > balance) throw new RuntimeException("Insufficient balance due to concurrent transaction");
            if (amount <= balance) {
                balance = balance - amount;
                return true;
            }
        } catch (Exception ex) {
            throw new RuntimeException("Subtract balance operation failed");
        } finally {
            lock.unlock();
        }
        return false;
    }
}