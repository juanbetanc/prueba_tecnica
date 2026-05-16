package com.example.prueba_tecnica.client.controller;

import com.example.prueba_tecnica.client.dto.request.ClientRequest;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.client.service.ClientService;
import com.example.prueba_tecnica.common.enums.IdentificationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @MockitoBean
    private ClientService clientService;

    @Test
    void shouldCreateClient() throws Exception {
        UUID clientId = UUID.randomUUID();

        ClientRequest request = new ClientRequest(
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.of(1995, 1, 1)
        );

        ClientResponse response = new ClientResponse(
                clientId,
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.of(1995, 1, 1),
                LocalDateTime.now(),
                null
        );

        when(clientService.create(any(ClientRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(clientId.toString()))
                .andExpect(jsonPath("$.identificationType").value("CC"))
                .andExpect(jsonPath("$.identificationNumber").value("123456789"))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.lastName").value("Pérez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.birthDate").value("1995-01-01"));
    }

    @Test
    void shouldFindAllClients() throws Exception {
        UUID clientId = UUID.randomUUID();

        ClientResponse response = new ClientResponse(
                clientId,
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.of(1995, 1, 1),
                LocalDateTime.now(),
                null
        );

        when(clientService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(clientId.toString()))
                .andExpect(jsonPath("$[0].identificationType").value("CC"))
                .andExpect(jsonPath("$[0].identificationNumber").value("123456789"))
                .andExpect(jsonPath("$[0].firstName").value("Juan"))
                .andExpect(jsonPath("$[0].lastName").value("Pérez"))
                .andExpect(jsonPath("$[0].email").value("juan@test.com"))
                .andExpect(jsonPath("$[0].birthDate").value("1995-01-01"));
    }

    @Test
    void shouldFindClientById() throws Exception {
        UUID clientId = UUID.randomUUID();

        ClientResponse response = new ClientResponse(
                clientId,
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.of(1995, 1, 1),
                LocalDateTime.now(),
                null
        );

        when(clientService.findById(clientId)).thenReturn(response);

        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId.toString()))
                .andExpect(jsonPath("$.identificationType").value("CC"))
                .andExpect(jsonPath("$.identificationNumber").value("123456789"))
                .andExpect(jsonPath("$.firstName").value("Juan"))
                .andExpect(jsonPath("$.lastName").value("Pérez"))
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.birthDate").value("1995-01-01"));
    }

    @Test
    void shouldUpdateClient() throws Exception {
        UUID clientId = UUID.randomUUID();

        ClientRequest request = new ClientRequest(
                IdentificationType.CC,
                "987654321",
                "Carlos",
                "Ramírez",
                "carlos@test.com",
                LocalDate.of(1990, 5, 10)
        );

        ClientResponse response = new ClientResponse(
                clientId,
                IdentificationType.CC,
                "987654321",
                "Carlos",
                "Ramírez",
                "carlos@test.com",
                LocalDate.of(1990, 5, 10),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(clientService.update(eq(clientId), any(ClientRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId.toString()))
                .andExpect(jsonPath("$.identificationType").value("CC"))
                .andExpect(jsonPath("$.identificationNumber").value("987654321"))
                .andExpect(jsonPath("$.firstName").value("Carlos"))
                .andExpect(jsonPath("$.lastName").value("Ramírez"))
                .andExpect(jsonPath("$.email").value("carlos@test.com"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-10"));
    }

    @Test
    void shouldDeleteClient() throws Exception {
        UUID clientId = UUID.randomUUID();

        doNothing().when(clientService).delete(clientId);

        mockMvc.perform(delete("/api/clients/{id}", clientId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestWhenCreateClientRequestIsInvalid() throws Exception {
        ClientRequest request = new ClientRequest(
                null,
                "",
                "J",
                "P",
                "correo-invalido",
                null
        );

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenUpdateClientRequestIsInvalid() throws Exception {
        UUID clientId = UUID.randomUUID();

        ClientRequest request = new ClientRequest(
                null,
                "",
                "J",
                "P",
                "correo-invalido",
                null
        );

        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}