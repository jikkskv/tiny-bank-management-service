package com.tinybank.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinybank.management.account.Account;
import com.tinybank.management.exception.CancelAccountException;
import com.tinybank.management.exception.CreateAccountException;
import com.tinybank.management.model.account.CreateAccountRequestModel;
import com.tinybank.management.service.AccountCrudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountCrudController.class)
class AccountCrudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountCrudService accountCrudService;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateAccountRequestModel createAccountRequestModel;

    @BeforeEach
    void setUp() {
        // Sample request model setup
        createAccountRequestModel = new CreateAccountRequestModel("John Doe", "johndoe", "password123", "user");
    }

    @Test
    void testCreateAccount_success() throws Exception {
        Account createdAccount = new Account();
        createdAccount.setAccountId(1L);

        when(accountCrudService.createAccount(any(Account.class))).thenReturn(createdAccount);

        mockMvc.perform(post("/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequestModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accountId").value(1L));
    }

    @Test
    void testCreateAccount_failure_dueToServiceException() throws Exception {
        when(accountCrudService.createAccount(any(Account.class))).thenThrow(new CreateAccountException("Account creation failed"));

        mockMvc.perform(post("/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequestModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("CREATE_ACCOUNT_FAILED"));
    }

    @Test
    void testCreateAccount_failure_dueToUnknownException() throws Exception {
        when(accountCrudService.createAccount(any(Account.class))).thenThrow(new IllegalArgumentException("Account creation failed"));

        mockMvc.perform(post("/createAccount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequestModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("SYSTEM_ERROR"));
    }

    @Test
    void testCancelAccount_success() throws Exception {
        when(accountCrudService.cancelAccount(1L)).thenReturn(true);

        mockMvc.perform(post("/cancelAccount")
                        .param("accountId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelAccount_failure_dueToServiceException() throws Exception {
        when(accountCrudService.cancelAccount(1L)).thenThrow(new CancelAccountException());

        mockMvc.perform(post("/cancelAccount")
                        .param("accountId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("CANCEL_ACCOUNT_FAILED"));
    }

    @Test
    void testCancelAccount_failure_dueToUnknownException() throws Exception {
        when(accountCrudService.cancelAccount(1L)).thenThrow(new IllegalArgumentException());

        mockMvc.perform(post("/cancelAccount")
                        .param("accountId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("SYSTEM_ERROR"));
    }

    @Test
    void testCancelAccount_failure_invalidAccountId() throws Exception {
        when(accountCrudService.cancelAccount(1L)).thenReturn(false);

        mockMvc.perform(post("/cancelAccount")
                        .param("accountId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("CANCEL_ACCOUNT_FAILED"));
    }
}