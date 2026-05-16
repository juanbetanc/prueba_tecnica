package com.example.prueba_tecnica.account.controller;

import com.example.prueba_tecnica.account.dto.request.AccountRequest;
import com.example.prueba_tecnica.account.dto.response.AccountResponse;
import com.example.prueba_tecnica.account.service.AccountService;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.common.enums.AccountStatus;
import com.example.prueba_tecnica.common.enums.AccountType;
import com.example.prueba_tecnica.common.enums.IdentificationType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private AccountService accountService;

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
            AccountStatus status,
            BigDecimal balance
    ) {
        return new AccountResponse(
                accountId,
                AccountType.SAVINGS,
                "5312345678",
                status,
                balance,
                false,
                LocalDateTime.now(),
                null,
                buildClientResponse(clientId)
        );
    }

    @Test
    void shouldCreateAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        AccountRequest request = new AccountRequest(
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                false,
                clientId
        );

        AccountResponse response = buildAccountResponse(
                accountId,
                clientId,
                AccountStatus.ACTIVE,
                BigDecimal.ZERO
        );

        when(accountService.create(any(AccountRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.type").value("SAVINGS"))
                .andExpect(jsonPath("$.accountNumber").value("5312345678"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.balance").value(0))
                .andExpect(jsonPath("$.gmfExempt").value(false))
                .andExpect(jsonPath("$.client.id").value(clientId.toString()))
                .andExpect(jsonPath("$.client.firstName").value("Juan"))
                .andExpect(jsonPath("$.client.email").value("juan@test.com"));
    }

    @Test
    void shouldFindAllAccounts() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        AccountResponse response = buildAccountResponse(
                accountId,
                clientId,
                AccountStatus.ACTIVE,
                BigDecimal.ZERO
        );

        when(accountService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(accountId.toString()))
                .andExpect(jsonPath("$[0].type").value("SAVINGS"))
                .andExpect(jsonPath("$[0].accountNumber").value("5312345678"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].client.id").value(clientId.toString()));
    }

    @Test
    void shouldFindAccountById() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        AccountResponse response = buildAccountResponse(
                accountId,
                clientId,
                AccountStatus.ACTIVE,
                BigDecimal.ZERO
        );

        when(accountService.findById(accountId)).thenReturn(response);

        mockMvc.perform(get("/api/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.type").value("SAVINGS"))
                .andExpect(jsonPath("$.accountNumber").value("5312345678"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.client.id").value(clientId.toString()));
    }

    @Test
    void shouldFindAccountsByClientId() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        AccountResponse response = buildAccountResponse(
                accountId,
                clientId,
                AccountStatus.ACTIVE,
                BigDecimal.ZERO
        );

        when(accountService.findByClientId(clientId)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/accounts/client/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(accountId.toString()))
                .andExpect(jsonPath("$[0].accountNumber").value("5312345678"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].client.id").value(clientId.toString()));
    }

    @Test
    void shouldActivateAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        AccountResponse response = buildAccountResponse(
                accountId,
                clientId,
                AccountStatus.ACTIVE,
                BigDecimal.ZERO
        );

        when(accountService.activate(accountId)).thenReturn(response);

        mockMvc.perform(patch("/api/accounts/{id}/activate", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldInactivateAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        AccountResponse response = buildAccountResponse(
                accountId,
                clientId,
                AccountStatus.INACTIVE,
                BigDecimal.ZERO
        );

        when(accountService.inactivate(accountId)).thenReturn(response);

        mockMvc.perform(patch("/api/accounts/{id}/inactivate", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void shouldCancelAccount() throws Exception {
        UUID accountId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        AccountResponse response = buildAccountResponse(
                accountId,
                clientId,
                AccountStatus.CANCELLED,
                BigDecimal.ZERO
        );

        when(accountService.cancel(accountId)).thenReturn(response);

        mockMvc.perform(patch("/api/accounts/{id}/cancel", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId.toString()))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateAccountRequestIsInvalid() throws Exception {
        AccountRequest request = new AccountRequest(
                null,
                null,
                null,
                null
        );

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenInitialBalanceIsNegative() throws Exception {
        UUID clientId = UUID.randomUUID();

        AccountRequest request = new AccountRequest(
                AccountType.SAVINGS,
                new BigDecimal("-1000"),
                false,
                clientId
        );

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}