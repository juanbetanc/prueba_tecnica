package com.example.prueba_tecnica.account.mapper;

import com.example.prueba_tecnica.account.dto.response.AccountResponse;
import com.example.prueba_tecnica.account.entity.Account;
import com.example.prueba_tecnica.client.mapper.ClientMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ClientMapper.class}
)
public interface AccountMapper {

    @Mapping(source = "client", target = "client")
    AccountResponse toResponse(Account account);
}
