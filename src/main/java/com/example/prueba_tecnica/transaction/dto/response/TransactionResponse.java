package com.example.prueba_tecnica.transaction.dto.response;

import com.example.prueba_tecnica.account.dto.response.AccountResponse;
import com.example.prueba_tecnica.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfterTransaction,
        AccountResponse sourceAccount,
        AccountResponse targetAccount,
        LocalDateTime createdAt
) {}
