package com.example.prueba_tecnica.account.service;

import com.example.prueba_tecnica.account.dto.request.AccountRequest;
import com.example.prueba_tecnica.account.dto.response.AccountResponse;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponse create(AccountRequest request);
    List<AccountResponse> findAll();
    AccountResponse findById(UUID id);
    List<AccountResponse> findByClientId(UUID clientId);
    AccountResponse activate(UUID id);
    AccountResponse inactivate(UUID id);
    AccountResponse cancel(UUID id);
}
