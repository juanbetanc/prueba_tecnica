package com.example.prueba_tecnica.account.controller;

import com.example.prueba_tecnica.account.dto.request.AccountRequest;
import com.example.prueba_tecnica.account.dto.response.AccountResponse;
import com.example.prueba_tecnica.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    //POST  /api/accounts
    //GET   /api/accounts
    //GET   /api/accounts/{id}
    //PATCH /api/accounts/{id}/activate
    //PATCH /api/accounts/{id}/inactivate
    //PATCH /api/accounts/{id}/cancel
    //GET   /api/accounts/client/{clientId}

    @PostMapping()
    public ResponseEntity<AccountResponse> save(
            @Valid @RequestBody AccountRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
    }

    @GetMapping()
    public ResponseEntity<List<AccountResponse>> getAll(){
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable UUID id){
        return ResponseEntity.ok(accountService.findById(id));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AccountResponse>> getByClientId(@PathVariable UUID clientId){
        return ResponseEntity.ok(accountService.findByClientId(clientId));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<AccountResponse> activate(@PathVariable UUID id){
        return ResponseEntity.ok(accountService.activate(id));
    }

    @PatchMapping("/{id}/inactivate")
    public ResponseEntity<AccountResponse> inactivate(@PathVariable UUID id){
        return ResponseEntity.ok(accountService.inactivate(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AccountResponse> cancel(@PathVariable UUID id){
        return ResponseEntity.ok(accountService.cancel(id));
    }
}
