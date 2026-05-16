package com.example.prueba_tecnica.client.service;

import com.example.prueba_tecnica.client.dto.request.ClientRequest;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientResponse create(ClientRequest request);
    ClientResponse update(UUID id, ClientRequest request);
    ClientResponse findById(UUID id);
    List<ClientResponse> findAll();
    void delete(UUID id);
}
