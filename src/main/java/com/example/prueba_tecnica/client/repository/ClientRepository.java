package com.example.prueba_tecnica.client.repository;

import com.example.prueba_tecnica.client.entity.Client;
import com.example.prueba_tecnica.common.enums.IdentificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    boolean existsByIdentificationTypeAndIdentificationNumber(
            IdentificationType identificationType,
            String identificationNumber
    );

    boolean existsByEmail(String email);
}
