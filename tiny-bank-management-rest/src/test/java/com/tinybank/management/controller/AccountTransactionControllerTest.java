package com.tinybank.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinybank.management.exception.DepositOperationException;
import com.tinybank.management.exception.TransferOperationException;
import com.tinybank.management.exception.WithdrawOperationException;
import com.tinybank.management.model.transaction.DualAccountTransactionRequestModel;
import com.tinybank.management.model.transaction.SingleAccountTransactionRequestModel;
import com.tinybank.management.service.AccountTransactionalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountTransactionController.class)
class AccountTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountTransactionalService accountTransactionalService;

    @Autowired
    private ObjectMapper objectMapper;

    private SingleAccountTransactionRequestModel depositRequest;
    private SingleAccountTransactionRequestModel withdrawRequest;
    private DualAccountTransactionRequestModel transferRequest;

    @BeforeEach
    void setUp() {
        depositRequest = new SingleAccountTransactionRequestModel(1L, 100.0, "deposit");
        withdrawRequest = new SingleAccountTransactionRequestModel(1L, 50.0, "withdraw");
        transferRequest = new DualAccountTransactionRequestModel(1L, 2L, 75.0, "transfer");
    }

    @Test
    void testDepositMoney_success() throws Exception {
        // No exception expected, simulating a successful deposit
        doNothing().when(accountTransactionalService).deposit(anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/depositMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testDepositMoney_failure_dueToDepositOperationException() throws Exception {
        doThrow(new DepositOperationException("Deposit failed")).when(accountTransactionalService).deposit(anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/depositMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("DEPOSIT_OPERATION_FAILED"));
    }

    @Test
    void testDepositMoney_failure_dueToUnknownException() throws Exception {
        doThrow(new IllegalArgumentException("Deposit failed")).when(accountTransactionalService).deposit(anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/depositMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("SYSTEM_ERROR"));
    }

    @Test
    void testWithdrawMoney_success() throws Exception {
        doNothing().when(accountTransactionalService).withdraw(anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/withdrawMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testWithdrawMoney_failure_dueToWithdrawOperationException() throws Exception {
        doThrow(new WithdrawOperationException("Withdraw failed")).when(accountTransactionalService).withdraw(anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/withdrawMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("WITHDRAW_OPERATION_FAILED"));
    }

    @Test
    void testWithdrawMoney_failure_dueToUnknownException() throws Exception {
        doThrow(new IllegalArgumentException("Withdraw failed")).when(accountTransactionalService).withdraw(anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/withdrawMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("SYSTEM_ERROR"));
    }

    @Test
    void testTransferMoney_success() throws Exception {
        doNothing().when(accountTransactionalService).transfer(anyLong(), anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/transferMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testTransferMoney_failure_dueToTransferOperationException() throws Exception {
        doThrow(new TransferOperationException("Transfer failed")).when(accountTransactionalService).transfer(anyLong(), anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/transferMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("TRANSFER_OPERATION_FAILED"));
    }

    @Test
    void testTransferMoney_failure_dueToUnknownException() throws Exception {
        doThrow(new IllegalArgumentException("Transfer failed")).when(accountTransactionalService).transfer(anyLong(), anyLong(), anyDouble(), anyString());

        mockMvc.perform(post("/transferMoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("SYSTEM_ERROR"));
    }
}