package com.tinybank.management.service.impl;

import com.tinybank.management.account.Account;
import com.tinybank.management.account.AccountStatus;
import com.tinybank.management.exception.DepositOperationException;
import com.tinybank.management.exception.InvalidAccountException;
import com.tinybank.management.exception.TransferOperationException;
import com.tinybank.management.exception.WithdrawOperationException;
import com.tinybank.management.service.AccountStorageDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        account.setAccountId(1L);
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertDoesNotThrow(() -> accountTransactionalService.deposit(fromAccountId, amount, "deposit"));
        assertEquals(amount, account.getBalance());
        verify(account).addBalance(amount, "deposit");
    }

    @Test
    void testDeposit_invalidAccountId() throws InvalidAccountException {
        assertThrows(DepositOperationException.class, () -> accountTransactionalService.deposit(fromAccountId, 123D, "deposit"));
        verify(accountStorageDB).getAccount(fromAccountId);
        verify(mock(Account.class), never()).addBalance(anyDouble(), eq("deposit"));
    }

    @Test
    void testDeposit_negativeAmount() throws InvalidAccountException {
        Account account = spy(new Account());
        double amount = -123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(DepositOperationException.class, () -> accountTransactionalService.deposit(fromAccountId, amount, "deposit"));
        verify(account).addBalance(amount, "deposit");
    }

    @Test
    void testDeposit_lockedAccount() throws InvalidAccountException {
        Account account = spy(new Account());
        account.setAccountStatus(AccountStatus.LOCKED);
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(DepositOperationException.class, () -> accountTransactionalService.deposit(fromAccountId, amount, "deposit"));
        verify(account).addBalance(amount, "deposit");
    }

    @Test
    void testWithdraw_success() throws Exception {
        Account account = spy(new Account());
        double currentBalance = 1000D;
        account.setAccountId(1L);
        account.addBalance(currentBalance, "deposit");
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertDoesNotThrow(() -> accountTransactionalService.withdraw(fromAccountId, amount, "withdraw"));
        assertEquals((currentBalance - amount), account.getBalance());
        verify(account).subtractBalance(amount, "withdraw");
    }

    @Test
    void testWithdraw_insufficientBalance() throws InvalidAccountException {
        Account account = spy(new Account());
        double currentBalance = 123D;
        account.setAccountId(1L);
        account.addBalance(currentBalance, "deposit");
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(WithdrawOperationException.class, () -> accountTransactionalService.withdraw(fromAccountId, amount + 1, "withdraw"));
        verify(account).subtractBalance(amount + 1, "withdraw");
    }

    @Test
    void testWithdraw_invalidAccountId() throws InvalidAccountException {
        assertThrows(WithdrawOperationException.class, () -> accountTransactionalService.withdraw(fromAccountId, 123D, "withdraw"));
        verify(accountStorageDB).getAccount(fromAccountId);
        verify(mock(Account.class), never()).subtractBalance(anyDouble(), eq("withdraw"));
    }

    @Test
    void testWithdraw_negativeAmount() throws InvalidAccountException {
        Account account = spy(new Account());
        double amount = -123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(WithdrawOperationException.class, () -> accountTransactionalService.withdraw(fromAccountId, amount, "withdraw"));
        verify(account).subtractBalance(amount, "withdraw");
    }

    @Test
    void testWithdraw_lockedAccount() throws InvalidAccountException {
        Account account = spy(new Account());
        account.setAccountStatus(AccountStatus.LOCKED);
        double amount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(account);

        assertThrows(WithdrawOperationException.class, () -> accountTransactionalService.withdraw(fromAccountId, amount, "withdraw"));
        verify(account).subtractBalance(amount, "withdraw");
    }

    @Test
    void testTransfer_success() throws Exception {
        Account fromAccount = spy(new Account());
        Account toAccount = spy(new Account());
        double currentBalance = 123D;
        fromAccount.setAccountId(1L);
        toAccount.setAccountId(1L);
        fromAccount.addBalance(currentBalance, "deposit");
        double transferAmount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(fromAccount);
        when(accountStorageDB.getAccount(toAccountId)).thenReturn(toAccount);

        assertDoesNotThrow(() -> accountTransactionalService.transfer(fromAccountId, toAccountId, transferAmount, "transfer"));
        assertEquals(0D, fromAccount.getBalance());
        assertEquals(123D, toAccount.getBalance());
        verify(fromAccount).subtractBalance(transferAmount, "transfer");
        verify(toAccount).addBalance(transferAmount, "transfer");
    }

    @Test
    void testTransfer_insufficientBalance() throws InvalidAccountException {
        Account fromAccount = spy(new Account());
        Account toAccount = spy(new Account());
        double currentBalance = 123D;
        fromAccount.setAccountId(1L);
        fromAccount.addBalance(currentBalance, "deposit");
        double transferAmount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(fromAccount);
        when(accountStorageDB.getAccount(toAccountId)).thenReturn(toAccount);

        assertThrows(TransferOperationException.class, () -> accountTransactionalService.transfer(fromAccountId, toAccountId, transferAmount + 1, "transfer"));
        assertEquals(123D, fromAccount.getBalance());
        assertEquals(0D, toAccount.getBalance());
        verify(fromAccount).subtractBalance(transferAmount + 1, "transfer");
        verify(toAccount, never()).addBalance(anyDouble(), eq("transfer"));
    }

    @Test
    void testTransfer_addBalanceFailure() throws InvalidAccountException {
        Account fromAccount = spy(new Account());
        Account toAccount = spy(new Account());
        double currentBalance = 123D;
        fromAccount.setAccountId(1L);
        toAccount.setAccountId(1L);
        fromAccount.addBalance(currentBalance, "deposit");
        double transferAmount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(fromAccount);
        when(accountStorageDB.getAccount(toAccountId)).thenReturn(toAccount);

        when(toAccount.addBalance(eq(transferAmount), eq("transfer"))).thenReturn(false);

        TransferOperationException exception = assertThrows(TransferOperationException.class, () ->
                accountTransactionalService.transfer(fromAccountId, toAccountId, transferAmount, "transfer")
        );

        assertEquals(123D, fromAccount.getBalance());
        assertEquals(0D, toAccount.getBalance());
        verify(fromAccount).subtractBalance(transferAmount, "transfer");
        verify(toAccount).addBalance(transferAmount, "transfer");
        verify(toAccount, times(1)).addBalance(transferAmount, "transfer"); // Ensuring the revert occurred
    }

    @Test
    void testTransfer_subtractOperationFalseStatus() throws InvalidAccountException {
        Account fromAccount = spy(new Account());
        Account toAccount = spy(new Account());
        double currentBalance = 123D;
        fromAccount.setAccountId(1L);
        fromAccount.addBalance(currentBalance, "deposit");
        double transferAmount = 123D;
        when(accountStorageDB.getAccount(fromAccountId)).thenReturn(fromAccount);
        when(accountStorageDB.getAccount(toAccountId)).thenReturn(toAccount);

        when(fromAccount.subtractBalance(eq(transferAmount), eq("withdraw"))).thenReturn(false);

        TransferOperationException exception = assertThrows(TransferOperationException.class, () ->
                accountTransactionalService.transfer(fromAccountId, toAccountId, transferAmount, "transfer")
        );

        assertEquals(123D, fromAccount.getBalance());
        assertEquals(0D, toAccount.getBalance());
        verify(fromAccount).subtractBalance(transferAmount, "transfer");
        verify(toAccount, never()).addBalance(transferAmount, "transfer");
    }
}