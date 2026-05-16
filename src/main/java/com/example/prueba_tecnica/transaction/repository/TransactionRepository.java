package com.example.prueba_tecnica.transaction.repository;

import com.example.prueba_tecnica.transaction.entity.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<FinancialTransaction, UUID> {
    List<FinancialTransaction> findBySourceAccountIdOrTargetAccountId(
            UUID sourceAccountId,
            UUID targetAccountId
    );
}
