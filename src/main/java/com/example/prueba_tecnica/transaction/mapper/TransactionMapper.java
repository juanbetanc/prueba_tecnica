package com.example.prueba_tecnica.transaction.mapper;

import com.example.prueba_tecnica.account.mapper.AccountMapper;
import com.example.prueba_tecnica.transaction.dto.response.TransactionResponse;
import com.example.prueba_tecnica.transaction.entity.FinancialTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = AccountMapper.class
)
public interface TransactionMapper {

    @Mapping(source = "sourceAccount", target = "sourceAccount")
    @Mapping(source = "targetAccount", target = "targetAccount")
    TransactionResponse toResponse(FinancialTransaction transaction);
}
