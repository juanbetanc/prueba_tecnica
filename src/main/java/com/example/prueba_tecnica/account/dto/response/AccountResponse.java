package com.example.prueba_tecnica.account.dto.response;

import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.common.enums.AccountStatus;
import com.example.prueba_tecnica.common.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        AccountType type,
        String accountNumber,
        AccountStatus status,
        BigDecimal balance,
        Boolean gmfExempt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ClientResponse client
) {
}
