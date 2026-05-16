package com.example.prueba_tecnica.client.service;

import com.example.prueba_tecnica.account.repository.AccountRepository;
import com.example.prueba_tecnica.client.dto.request.ClientRequest;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.client.entity.Client;
import com.example.prueba_tecnica.client.mapper.ClientMapper;
import com.example.prueba_tecnica.client.repository.ClientRepository;
import com.example.prueba_tecnica.exception.BusinessException;
import com.example.prueba_tecnica.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponse create(ClientRequest request) {
        validateAdult(request.birthDate());
        validateUniqueIdentification(request);
        validateUniqueEmail(request.email());

        Client client = clientMapper.toEntity(request);

        Client saved = clientRepository.save(client);

        return clientMapper.toResponse(saved);
    }

    @Override
    public List<ClientResponse> findAll() {
        return clientRepository.findAll()
                .stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Override
    public ClientResponse findById(UUID id) {
        Client client = getClientById(id);

        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponse update(UUID id, ClientRequest request) {
        Client client = getClientById(id);

        validateAdult(request.birthDate());

        clientMapper.updateEntityFromRequest(request, client);

        Client saved = clientRepository.save(client);

        return clientMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        Client client = getClientById(id);

        if (accountRepository.existsByClientId(id)) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene productos vinculados.");
        }

        clientRepository.delete(client);
    }

    private Client getClientById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(
                        "No se encontró el cliente con el id: " + id
                ));
    }

    private void validateAdult(LocalDate birthDate) {
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < 18) {
            throw new BusinessException("El cliente debe ser mayor de edad.");
        }
    }

    private void validateUniqueIdentification(ClientRequest request) {
        boolean exists = clientRepository.existsByIdentificationTypeAndIdentificationNumber(
                request.identificationType(),
                request.identificationNumber()
        );

        if (exists) {
            throw new BusinessException("Ya existe un cliente con ese tipo y número de identificación.");
        }
    }

    private void validateUniqueEmail(String email) {
        if (clientRepository.existsByEmail(email)) {
            throw new BusinessException("Ya existe un cliente con ese correo electrónico.");
        }
    }
}