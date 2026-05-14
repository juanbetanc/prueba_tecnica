package com.example.prueba_tecnica.transaction.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;

public record DepositRequest(
        @NotNull UUID accountId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount
) {
}
