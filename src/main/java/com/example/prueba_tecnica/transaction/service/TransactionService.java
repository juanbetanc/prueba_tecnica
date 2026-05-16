package com.example.prueba_tecnica.transaction.service;

import com.example.prueba_tecnica.account.entity.Account;
import com.example.prueba_tecnica.transaction.dto.request.DepositRequest;
import com.example.prueba_tecnica.transaction.dto.request.TransferRequest;
import com.example.prueba_tecnica.transaction.dto.request.WithdrawRequest;
import com.example.prueba_tecnica.transaction.dto.response.TransactionResponse;
import com.example.prueba_tecnica.transaction.entity.FinancialTransaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponse deposit(DepositRequest request);
    TransactionResponse withdraw(WithdrawRequest request);
    List<TransactionResponse> transfer(TransferRequest request);
    List<TransactionResponse> getByAccount(UUID id);
}
