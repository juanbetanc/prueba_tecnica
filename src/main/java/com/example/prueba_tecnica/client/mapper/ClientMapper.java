package com.example.prueba_tecnica.client.mapper;

import com.example.prueba_tecnica.client.dto.request.ClientRequest;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.client.entity.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toEntity(ClientRequest request);
    ClientResponse toResponse(Client client);
}
