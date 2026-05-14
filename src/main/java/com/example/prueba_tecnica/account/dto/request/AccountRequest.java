package com.example.prueba_tecnica.account.dto.request;

import com.example.prueba_tecnica.common.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;


import java.math.BigDecimal;
import java.util.UUID;

public record AccountRequest(
        @NotNull AccountType type,

        @NotNull
        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal initialBalance,

        @NotNull Boolean gmfExempt,

        @NotNull UUID clientId
) {
}
