package com.example.prueba_tecnica.account.repository;

import com.example.prueba_tecnica.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByAccountNumber(String accountNumber);
    boolean existsByClientId(UUID clientId);
    List<Account> findByClientId(UUID clientId);
}
