package com.example.prueba_tecnica.transaction.controller;

import com.example.prueba_tecnica.account.dto.response.AccountResponse;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.common.enums.AccountStatus;
import com.example.prueba_tecnica.common.enums.AccountType;
import com.example.prueba_tecnica.common.enums.IdentificationType;
import com.example.prueba_tecnica.common.enums.TransactionType;
import com.example.prueba_tecnica.transaction.dto.request.DepositRequest;
import com.example.prueba_tecnica.transaction.dto.request.TransferRequest;
import com.example.prueba_tecnica.transaction.dto.request.WithdrawRequest;
import com.example.prueba_tecnica.transaction.dto.response.TransactionResponse;
import com.example.prueba_tecnica.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private TransactionService transactionService;

    private ClientResponse buildClientResponse(UUID clientId) {
        return new ClientResponse(
                clientId,
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.of(1995, 1, 1),
                LocalDateTime.now(),
                null
        );
    }

    private AccountResponse buildAccountResponse(
            UUID accountId,
            UUID clientId,
            String accountNumber,
            BigDecimal balance
    ) {
        return new AccountResponse(
                accountId,
                AccountType.SAVINGS,
                accountNumber,
                AccountStatus.ACTIVE,
                balance,
                false,
                LocalDateTime.now(),
                null,
                buildClientResponse(clientId)
        );
    }

    @Test
    void shouldDeposit() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        DepositRequest request = new DepositRequest(
                accountId,
                new BigDecimal("50000")
        );

        AccountResponse sourceAccount = buildAccountResponse(
                accountId,
                clientId,
                "5312345678",
                new BigDecimal("150000")
        );

        TransactionResponse response = new TransactionResponse(
                transactionId,
                TransactionType.DEPOSIT,
                new BigDecimal("50000"),
                new BigDecimal("150000"),
                sourceAccount,
                null,
                LocalDateTime.now()
        );

        when(transactionService.deposit(any(DepositRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.type").value("DEPOSIT"))
                .andExpect(jsonPath("$.amount").value(50000))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(150000))
                .andExpect(jsonPath("$.sourceAccount.id").value(accountId.toString()))
                .andExpect(jsonPath("$.sourceAccount.accountNumber").value("5312345678"))
                .andExpect(jsonPath("$.sourceAccount.balance").value(150000))
                .andExpect(jsonPath("$.targetAccount").doesNotExist());
    }

    @Test
    void shouldWithdraw() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        WithdrawRequest request = new WithdrawRequest(
                accountId,
                new BigDecimal("30000")
        );

        AccountResponse sourceAccount = buildAccountResponse(
                accountId,
                clientId,
                "5312345678",
                new BigDecimal("70000")
        );

        TransactionResponse response = new TransactionResponse(
                transactionId,
                TransactionType.WITHDRAWAL,
                new BigDecimal("30000"),
                new BigDecimal("70000"),
                sourceAccount,
                null,
                LocalDateTime.now()
        );

        when(transactionService.withdraw(any(WithdrawRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transactionId.toString()))
                .andExpect(jsonPath("$.type").value("WITHDRAWAL"))
                .andExpect(jsonPath("$.amount").value(30000))
                .andExpect(jsonPath("$.balanceAfterTransaction").value(70000))
                .andExpect(jsonPath("$.sourceAccount.id").value(accountId.toString()))
                .andExpect(jsonPath("$.sourceAccount.accountNumber").value("5312345678"))
                .andExpect(jsonPath("$.sourceAccount.balance").value(70000))
                .andExpect(jsonPath("$.targetAccount").doesNotExist());
    }

    @Test
    void shouldTransfer() throws Exception {
        UUID sourceAccountId = UUID.randomUUID();
        UUID targetAccountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        UUID debitTransactionId = UUID.randomUUID();
        UUID creditTransactionId = UUID.randomUUID();

        TransferRequest request = new TransferRequest(
                sourceAccountId,
                targetAccountId,
                new BigDecimal("40000")
        );

        AccountResponse sourceAccount = buildAccountResponse(
                sourceAccountId,
                clientId,
                "5312345678",
                new BigDecimal("60000")
        );

        AccountResponse targetAccount = buildAccountResponse(
                targetAccountId,
                clientId,
                "5398765432",
                new BigDecimal("90000")
        );

        TransactionResponse debitResponse = new TransactionResponse(
                debitTransactionId,
                TransactionType.TRANSFER_DEBIT,
                new BigDecimal("40000"),
                new BigDecimal("60000"),
                sourceAccount,
                targetAccount,
                LocalDateTime.now()
        );

        TransactionResponse creditResponse = new TransactionResponse(
                creditTransactionId,
                TransactionType.TRANSFER_CREDIT,
                new BigDecimal("40000"),
                new BigDecimal("90000"),
                sourceAccount,
                targetAccount,
                LocalDateTime.now()
        );

        when(transactionService.transfer(any(TransferRequest.class)))
                .thenReturn(List.of(debitResponse, creditResponse));

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(debitTransactionId.toString()))
                .andExpect(jsonPath("$[0].type").value("TRANSFER_DEBIT"))
                .andExpect(jsonPath("$[0].amount").value(40000))
                .andExpect(jsonPath("$[0].balanceAfterTransaction").value(60000))
                .andExpect(jsonPath("$[0].sourceAccount.id").value(sourceAccountId.toString()))
                .andExpect(jsonPath("$[0].sourceAccount.accountNumber").value("5312345678"))
                .andExpect(jsonPath("$[0].targetAccount.id").value(targetAccountId.toString()))
                .andExpect(jsonPath("$[0].targetAccount.accountNumber").value("5398765432"))

                .andExpect(jsonPath("$[1].id").value(creditTransactionId.toString()))
                .andExpect(jsonPath("$[1].type").value("TRANSFER_CREDIT"))
                .andExpect(jsonPath("$[1].amount").value(40000))
                .andExpect(jsonPath("$[1].balanceAfterTransaction").value(90000))
                .andExpect(jsonPath("$[1].sourceAccount.id").value(sourceAccountId.toString()))
                .andExpect(jsonPath("$[1].targetAccount.id").value(targetAccountId.toString()));
    }

    @Test
    void shouldFindTransactionsByAccount() throws Exception {
        UUID transactionId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        AccountResponse sourceAccount = buildAccountResponse(
                accountId,
                clientId,
                "5312345678",
                new BigDecimal("150000")
        );

        TransactionResponse response = new TransactionResponse(
                transactionId,
                TransactionType.DEPOSIT,
                new BigDecimal("50000"),
                new BigDecimal("150000"),
                sourceAccount,
                null,
                LocalDateTime.now()
        );

        when(transactionService.getByAccount(accountId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/transactions/account/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(transactionId.toString()))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(50000))
                .andExpect(jsonPath("$[0].balanceAfterTransaction").value(150000))
                .andExpect(jsonPath("$[0].sourceAccount.id").value(accountId.toString()))
                .andExpect(jsonPath("$[0].sourceAccount.accountNumber").value("5312345678"))
                .andExpect(jsonPath("$[0].targetAccount").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestWhenDepositRequestIsInvalid() throws Exception {
        DepositRequest request = new DepositRequest(
                null,
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/api/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenWithdrawRequestIsInvalid() throws Exception {
        WithdrawRequest request = new WithdrawRequest(
                null,
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenTransferRequestIsInvalid() throws Exception {
        TransferRequest request = new TransferRequest(
                null,
                null,
                BigDecimal.ZERO
        );

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}