package com.example.prueba_tecnica.client.dto.request;

import com.example.prueba_tecnica.common.enums.IdentificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClientRequest(
        @NotNull IdentificationType identificationType,

        @NotBlank
        String identificationNumber,

        @NotBlank
        @Size(min = 2)
        String firstName,

        @NotBlank
        @Size(min = 2)
        String lastName,

        @NotBlank
        @Email
        String email,

        @NotNull
        LocalDate birthDate
) {
}
