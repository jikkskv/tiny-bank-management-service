package com.tinybank.management.service.impl;


import com.tinybank.management.account.Account;
import com.tinybank.management.exception.CancelAccountException;
import com.tinybank.management.exception.CreateAccountException;
import com.tinybank.management.service.AccountStorageDB;
import com.tinybank.management.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountCrudServiceImplTest {

    @Mock
    private AccountStorageDB accountStorageDB;

    @InjectMocks
    private AccountCrudServiceImpl accountCrudService;

    private Account account;

    private static final User user = User.builder().build();

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setUser(user);
    }

    @Test
    void testCreateAccount_validInput() throws Exception {
        when(accountStorageDB.addAccount(any(Account.class))).thenReturn(account);

        Account result = accountCrudService.createAccount(account);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getAccountId());
        assertEquals(1L, result.getAccountId());
        verify(accountStorageDB).addAccount(any(Account.class));
    }

    @Test
    void testCreateAccount_noUserSet() {
        Account invalidAccount = new Account(); // No user set
        assertThrows(CreateAccountException.class, () -> accountCrudService.createAccount(invalidAccount));
    }

    @Test
    void testCreateAccount_nullInput() {
        assertThrows(CreateAccountException.class, () -> accountCrudService.createAccount(null));
    }

    @Test
    void testCreateAccount_saveFailed() {
        when(accountStorageDB.addAccount(any(Account.class))).thenReturn(null);
        assertThrows(CreateAccountException.class, () -> accountCrudService.createAccount(account));
    }

    @Test
    void testCancelAccount_validId() throws Exception {
        when(accountStorageDB.deleteAccount(anyLong())).thenReturn(true);

        boolean result = accountCrudService.cancelAccount(1L);

        assertTrue(result);
        verify(accountStorageDB).deleteAccount(anyLong());
    }

    @Test
    void testCancelAccount_nullAccountId() throws Exception {
        boolean result = accountCrudService.cancelAccount(null);

        assertFalse(result);
        verify(accountStorageDB, never()).deleteAccount(anyLong());
    }

    @Test
    void testCancelAccountDeletionFailure() {
        when(accountStorageDB.deleteAccount(anyLong())).thenThrow(new RuntimeException());

        assertThrows(CancelAccountException.class, () -> accountCrudService.cancelAccount(1L));
    }
}