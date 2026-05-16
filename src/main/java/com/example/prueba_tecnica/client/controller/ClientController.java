package com.example.prueba_tecnica.client.controller;

import com.example.prueba_tecnica.client.dto.request.ClientRequest;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    //POST   /api/clients
    //GET    /api/clients
    //GET    /api/clients/{id}
    //PUT    /api/clients/{id}
    //DELETE /api/clients/{id}

    @PostMapping()
    public ResponseEntity<ClientResponse> save(
            @Valid @RequestBody ClientRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                clientService.create(request)
        );
    }

    @GetMapping()
    public ResponseEntity<List<ClientResponse>> getAll(){
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getById(@PathVariable UUID id){
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ClientRequest request
    ){
        return ResponseEntity.ok(clientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
