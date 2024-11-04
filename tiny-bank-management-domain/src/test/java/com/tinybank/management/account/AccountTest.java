package com.tinybank.management.account;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @Test
    void testAddBalance_success() {
        Account account = new Account();
        account.setAccountId(1L);
        double amount = 100.0;
        boolean result = account.addBalance(amount);

        assertTrue(result, "The balance should be added successfully.");
        assertEquals(100.0, account.getBalance(), "Balance should be 100 after adding 100.");
        assertEquals(1, account.getTransactions().size(), "There should be 1 transaction recorded.");
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().get(0).getTransactionType(), "Transaction type should be DEPOSIT.");
    }

    @Test
    void testAddBalance_failedDueToNegativeAmount() {
        Account account = new Account();
        account.setAccountId(1L);
        double amount = -50.0;
        boolean result = account.addBalance(amount);

        assertFalse(result, "The balance should not be added for negative amounts.");
        assertEquals(0.0, account.getBalance(), "Balance should remain 0 if addition fails.");
        assertEquals(0, account.getTransactions().size(), "No transaction should be recorded.");
    }

    @Test
    void testAddBalance_failedDueToLockedAccountStatus() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setAccountStatus(AccountStatus.LOCKED);
        boolean result = account.addBalance(100.0);

        assertFalse(result, "The balance should not be added if the account status is not AVAILABLE.");
        assertEquals(0.0, account.getBalance(), "Balance should remain 0 if addition fails.");
        assertEquals(0, account.getTransactions().size(), "No transaction should be recorded.");
    }

    @Test
    void testSubtractBalance_success() {
        Account account = new Account();
        account.setAccountId(1L);
        account.addBalance(200.0);  // Adding initial balance

        boolean result = account.subtractBalance(100.0);

        assertTrue(result, "The balance should be subtracted successfully.");
        assertEquals(100.0, account.getBalance(), "Balance should be 100 after subtracting 100 from 200.");
        assertEquals(2, account.getTransactions().size(), "There should be 2 transactions recorded.");
        assertEquals(TransactionType.WITHDRAW, account.getTransactions().get(1).getTransactionType(), "Transaction type should be WITHDRAW.");
    }

    @Test
    void testSubtractBalance_failedDueToInsufficientFunds() {
        Account account = new Account();
        account.setAccountId(1L);
        account.addBalance(50.0);  // Adding initial balance

        Exception exception = assertThrows(RuntimeException.class, () -> account.subtractBalance(100.0));
        assertEquals("Insufficient balance", exception.getMessage());
        assertEquals(50.0, account.getBalance(), "Balance should remain unchanged after failed subtraction.");
        assertEquals(1, account.getTransactions().size(), "Only the deposit transaction should be recorded.");
    }

    @Test
    void testSubtractBalance_failedDueToNegativeAmount() {
        Account account = new Account();
        account.setAccountId(1L);
        boolean result = account.subtractBalance(-10.0);

        assertFalse(result, "The balance should not be subtracted for negative amounts.");
        assertEquals(0.0, account.getBalance(), "Balance should remain unchanged.");
        assertEquals(0, account.getTransactions().size(), "No transaction should be recorded.");
    }

    @Test
    void testSubtractBalance_failedDueToLockedAccountStatus() {
        Account account = new Account();
        account.setAccountId(1L);
        account.addBalance(100.0);
        account.setAccountStatus(AccountStatus.LOCKED);

        boolean result = account.subtractBalance(50.0);

        assertFalse(result, "The balance should not be subtracted if the account status is not AVAILABLE.");
        assertEquals(100.0, account.getBalance(), "Balance should remain unchanged.");
        assertEquals(1, account.getTransactions().size(), "Only the deposit transaction should be recorded.");
    }

    @Test
    void testSubtractBalance_testConcurrentWithdrawl() throws InterruptedException {
        Account account = new Account();
        account.setAccountId(1L);
        account.addBalance(500.0);
        int numberOfThreads = 40;

        // Lock the account manually to simulate a concurrent transaction
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int cntr = 0; cntr < numberOfThreads; cntr++) {
            futures.add(executorService.submit(() -> {
                try {
                    return account.subtractBalance(50.0);
                } catch (Exception e) {
                    return false;
                }
            }));
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        int successfulWithdrawals = 0;
        for (Future<Boolean> future : futures) {
            try {
                if (future.get()) {
                    successfulWithdrawals++;
                }
            } catch (ExecutionException e) {
                // Ignoring any ExecutionExceptions for this test
            }
        }
        // Give the other thread some time to acquire the lock
        assertEquals(10, successfulWithdrawals, "Only 500/50=10 threads should be able to withdraw successfully.");
        assertEquals(11, account.getTransactions().size(), "Only the initial deposit transaction + 10 withdrawl transactions should be recorded.");
    }
}