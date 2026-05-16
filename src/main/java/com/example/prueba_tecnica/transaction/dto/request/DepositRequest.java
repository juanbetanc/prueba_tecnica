package com.example.prueba_tecnica.transaction.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.util.UUID;

public record DepositRequest(
        @NotNull UUID accountId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount
) {
}
