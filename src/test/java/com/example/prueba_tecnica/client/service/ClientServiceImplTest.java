package com.example.prueba_tecnica.client.service;

import com.example.prueba_tecnica.account.repository.AccountRepository;
import com.example.prueba_tecnica.client.dto.request.ClientRequest;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.client.entity.Client;
import com.example.prueba_tecnica.client.mapper.ClientMapper;
import com.example.prueba_tecnica.client.repository.ClientRepository;
import com.example.prueba_tecnica.common.enums.IdentificationType;
import com.example.prueba_tecnica.exception.BusinessException;
import com.example.prueba_tecnica.exception.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    private UUID clientId;
    private Client client;
    private ClientResponse clientResponse;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        client = Client.builder()
                .id(clientId)
                .identificationType(IdentificationType.CC)
                .identificationNumber("123456789")
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@test.com")
                .birthDate(LocalDate.of(1995, 1, 1))
                .build();

        clientResponse = new ClientResponse(
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
    }

    @Test
    void shouldCreateClientSuccessfully() {
        ClientRequest request = new ClientRequest(
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.of(1995, 1, 1)
        );

        when(clientRepository.existsByIdentificationTypeAndIdentificationNumber(
                request.identificationType(),
                request.identificationNumber()
        )).thenReturn(false);

        when(clientRepository.existsByEmail(request.email())).thenReturn(false);
        when(clientMapper.toEntity(request)).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        ClientResponse result = clientService.create(request);

        assertNotNull(result);
        assertEquals(clientId, result.id());
        assertEquals("Juan", result.firstName());
        assertEquals("juan@test.com", result.email());

        verify(clientRepository).save(any(Client.class));
        verify(clientMapper).toResponse(client);
    }

    @Test
    void shouldThrowExceptionWhenClientIsMinor() {
        ClientRequest request = new ClientRequest(
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.now().minusYears(17)
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.create(request)
        );

        assertEquals("El cliente debe ser mayor de edad.", exception.getMessage());

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void shouldThrowExceptionWhenIdentificationAlreadyExists() {
        ClientRequest request = new ClientRequest(
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.of(1995, 1, 1)
        );

        when(clientRepository.existsByIdentificationTypeAndIdentificationNumber(
                request.identificationType(),
                request.identificationNumber()
        )).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.create(request)
        );

        assertEquals("Ya existe un cliente con ese tipo y número de identificación.", exception.getMessage());

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        ClientRequest request = new ClientRequest(
                IdentificationType.CC,
                "123456789",
                "Juan",
                "Pérez",
                "juan@test.com",
                LocalDate.of(1995, 1, 1)
        );

        when(clientRepository.existsByIdentificationTypeAndIdentificationNumber(
                request.identificationType(),
                request.identificationNumber()
        )).thenReturn(false);

        when(clientRepository.existsByEmail(request.email())).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.create(request)
        );

        assertEquals("Ya existe un cliente con ese correo electrónico.", exception.getMessage());

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void shouldFindClientByIdSuccessfully() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        ClientResponse result = clientService.findById(clientId);

        assertNotNull(result);
        assertEquals(clientId, result.id());
        assertEquals("Juan", result.firstName());

        verify(clientRepository).findById(clientId);
        verify(clientMapper).toResponse(client);
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExist() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> clientService.findById(clientId)
        );

        assertEquals("No se encontró el cliente con el id: " + clientId, exception.getMessage());

        verify(clientRepository).findById(clientId);
        verify(clientMapper, never()).toResponse(any(Client.class));
    }

    @Test
    void shouldFindAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(client));
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        List<ClientResponse> result = clientService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(clientId, result.get(0).id());

        verify(clientRepository).findAll();
        verify(clientMapper).toResponse(client);
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        ClientRequest request = new ClientRequest(
                IdentificationType.CC,
                "987654321",
                "Carlos",
                "Ramírez",
                "carlos@test.com",
                LocalDate.of(1990, 5, 10)
        );

        ClientResponse updatedResponse = new ClientResponse(
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

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateEntityFromRequest(request, client);
        when(clientRepository.save(client)).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(updatedResponse);

        ClientResponse result = clientService.update(clientId, request);

        assertNotNull(result);
        assertEquals("Carlos", result.firstName());
        assertEquals("carlos@test.com", result.email());

        verify(clientRepository).findById(clientId);
        verify(clientMapper).updateEntityFromRequest(request, client);
        verify(clientRepository).save(client);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingClientToMinorAge() {
        ClientRequest request = new ClientRequest(
                IdentificationType.CC,
                "987654321",
                "Carlos",
                "Ramírez",
                "carlos@test.com",
                LocalDate.now().minusYears(16)
        );

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.update(clientId, request)
        );

        assertEquals("El cliente debe ser mayor de edad.", exception.getMessage());

        verify(clientRepository).findById(clientId);
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void shouldDeleteClientSuccessfully() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.existsByClientId(clientId)).thenReturn(false);

        clientService.delete(clientId);

        verify(clientRepository).findById(clientId);
        verify(accountRepository).existsByClientId(clientId);
        verify(clientRepository).delete(client);
    }

    @Test
    void shouldThrowExceptionWhenDeletingClientWithAccounts() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.existsByClientId(clientId)).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> clientService.delete(clientId)
        );

        assertEquals("No se puede eliminar el cliente porque tiene productos vinculados.", exception.getMessage());

        verify(clientRepository).findById(clientId);
        verify(accountRepository).existsByClientId(clientId);
        verify(clientRepository, never()).delete(any(Client.class));
    }
}