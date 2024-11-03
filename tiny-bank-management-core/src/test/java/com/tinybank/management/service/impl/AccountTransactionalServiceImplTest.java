package com.tinybank.management.service.impl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import com.tinybank.management.account.Account;
import com.tinybank.management.account.AccountStatus;
import com.tinybank.management.exception.DepositOperationException;
import com.tinybank.management.exception.InvalidAccountException;
import com.tinybank.management.exception.TransferOperationException;
import com.tinybank.management.exception.WithdrawOperationException;
import com.tinybank.management.service.AccountStorageDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountTransactionalServiceImplTest {

    @Mock
    private AccountStorageDB accountStorageDB;

    @InjectMocks
    private AccountTransactionalServiceImpl accountTransactionalService;

    private final Long fromAccountId = 1L;
    private final Long toAccountId = 2L;

    @Test
    void testDeposit_success() throws InvalidAccountException {
        Account account = spy(new Account());
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertDoesNotThrow(() -> accountTransactionalService.deposit(fromAccountId, amount));
        assertEquals(amount, account.getBalance());
        verify(account).addBalance(amount);
    }

    @Test
    void testDeposit_invalidAccountId() throws InvalidAccountException {
        assertThrows(DepositOperationException.class, () -> accountTransactionalService.deposit(fromAccountId, 123D));
        verify(accountStorageDB).getAccount(fromAccountId);
        verify(mock(Account.class), never()).addBalance(anyDouble());
    }

    @Test
    void testDeposit_negativeAmount() throws InvalidAccountException {
        Account account = spy(new Account());
        double amount = -123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(DepositOperationException.class, () -> accountTransactionalService.deposit(fromAccountId, amount));
        verify(account).addBalance(amount);
    }

    @Test
    void testDeposit_lockedAccount() throws InvalidAccountException {
        Account account = spy(new Account());
        account.setAccountStatus(AccountStatus.LOCKED);
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(DepositOperationException.class, () -> accountTransactionalService.deposit(fromAccountId, amount));
        verify(account).addBalance(amount);
    }

    @Test
    void testWithdraw_success() throws Exception {
        Account account = spy(new Account());
        double currentBalance = 1000D;
        account.addBalance(currentBalance);
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertDoesNotThrow(() -> accountTransactionalService.withdraw(fromAccountId, amount));
        assertEquals((currentBalance - amount), account.getBalance());
        verify(account).subtractBalance(amount);
    }

    @Test
    void testWithdraw_insufficientBalance() throws InvalidAccountException {
        Account account = spy(new Account());
        double currentBalance = 123D;
        account.addBalance(currentBalance);
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(WithdrawOperationException.class, () -> accountTransactionalService.withdraw(fromAccountId, amount+1));
        verify(account).subtractBalance(amount+1);
    }

    @Test
    void testWithdraw_invalidAccountId() throws InvalidAccountException {
        assertThrows(WithdrawOperationException.class, () -> accountTransactionalService.withdraw(fromAccountId, 123D));
        verify(accountStorageDB).getAccount(fromAccountId);
        verify(mock(Account.class), never()).subtractBalance(anyDouble());
    }

    @Test
    void testWithdraw_negativeAmount() throws InvalidAccountException {
        Account account = spy(new Account());
        double amount = -123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(WithdrawOperationException.class, () -> accountTransactionalService.withdraw(fromAccountId, amount));
        verify(account).subtractBalance(amount);
    }

    @Test
    void testWithdraw_lockedAccount() throws InvalidAccountException {
        Account account = spy(new Account());
        account.setAccountStatus(AccountStatus.LOCKED);
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(WithdrawOperationException.class, () -> accountTransactionalService.withdraw(fromAccountId, amount));
        verify(account).subtractBalance(amount);
    }

    @Test
    void testTransfer_success() throws Exception {
        Account fromAccount = spy(new Account());
        Account toAccount = spy(new Account());
        double currentBalance = 123D;
        fromAccount.addBalance(currentBalance);
        double transferAmount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(fromAccount);
        when(accountStorageDB.getAccount(toAccountId)).thenReturn(toAccount);

        assertDoesNotThrow(() -> accountTransactionalService.transfer(fromAccountId, toAccountId, transferAmount));
        assertEquals(0D, fromAccount.getBalance());
        assertEquals(123D, toAccount.getBalance());
        verify(fromAccount).subtractBalance(transferAmount);
        verify(toAccount).addBalance(transferAmount);
    }

    @Test
    void testTransfer_insufficientBalance() throws InvalidAccountException {
        Account fromAccount = spy(new Account());
        Account toAccount = spy(new Account());
        double currentBalance = 123D;
        fromAccount.addBalance(currentBalance);
        double transferAmount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(fromAccount);
        when(accountStorageDB.getAccount(toAccountId)).thenReturn(toAccount);

        assertThrows(TransferOperationException.class, () -> accountTransactionalService.transfer(fromAccountId, toAccountId, transferAmount+1));
        assertEquals(123D, fromAccount.getBalance());
        assertEquals(0D, toAccount.getBalance());
        verify(fromAccount).subtractBalance(transferAmount+1);
        verify(toAccount, never()).addBalance(anyDouble());
    }

    @Test
    void testTransfer_addBalanceFailure() throws InvalidAccountException {
        Account fromAccount = spy(new Account());
        Account toAccount = spy(new Account());
        double currentBalance = 123D;
        fromAccount.addBalance(currentBalance);
        double transferAmount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(fromAccount);
        when(accountStorageDB.getAccount(toAccountId)).thenReturn(toAccount);

        when(toAccount.addBalance(eq(transferAmount))).thenReturn(false);

        TransferOperationException exception = assertThrows(TransferOperationException.class, () ->
                accountTransactionalService.transfer(fromAccountId, toAccountId, transferAmount)
        );

        assertEquals(123D, fromAccount.getBalance());
        assertEquals(0D, toAccount.getBalance());
        verify(fromAccount).subtractBalance(transferAmount);
        verify(toAccount).addBalance(transferAmount);
        verify(toAccount, times(1)).addBalance(transferAmount); // Ensuring the revert occurred
    }

    @Test
    void testTransfer_subtractOperationFalseStatus() throws InvalidAccountException {
        Account fromAccount = spy(new Account());
        Account toAccount = spy(new Account());
        double currentBalance = 123D;
        fromAccount.addBalance(currentBalance);
        double transferAmount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(fromAccount);
        when(accountStorageDB.getAccount(toAccountId)).thenReturn(toAccount);

        when(fromAccount.subtractBalance(eq(transferAmount))).thenReturn(false);

        TransferOperationException exception = assertThrows(TransferOperationException.class, () ->
                accountTransactionalService.transfer(fromAccountId, toAccountId, transferAmount)
        );

        assertEquals(123D, fromAccount.getBalance());
        assertEquals(0D, toAccount.getBalance());
        verify(fromAccount).subtractBalance(transferAmount);
        verify(toAccount, never()).addBalance(transferAmount);
    }
}