package com.example.prueba_tecnica.transaction.controller;

import com.example.prueba_tecnica.transaction.dto.request.DepositRequest;
import com.example.prueba_tecnica.transaction.dto.request.TransferRequest;
import com.example.prueba_tecnica.transaction.dto.request.WithdrawRequest;
import com.example.prueba_tecnica.transaction.dto.response.TransactionResponse;
import com.example.prueba_tecnica.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    //POST /api/transactions/deposit
    //POST /api/transactions/withdraw
    //POST /api/transactions/transfer
    //GET  /api/transactions/account/{accountId}

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@Valid @RequestBody DepositRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@Valid @RequestBody WithdrawRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<TransactionResponse>> transfer(@Valid @RequestBody TransferRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.transfer(request));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getByAccount(@PathVariable UUID accountId){
        return ResponseEntity.ok(transactionService.getByAccount(accountId));
    }
}
