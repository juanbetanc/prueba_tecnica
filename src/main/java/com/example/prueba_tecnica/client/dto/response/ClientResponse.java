package com.example.prueba_tecnica.client.dto.response;

import com.example.prueba_tecnica.common.enums.IdentificationType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        IdentificationType identificationType,
        String identificationNumber,
        String firstName,
        String lastName,
        String email,
        LocalDate birthDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
