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
public class ClientServiceImpl implements ClientService{
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponse create(ClientRequest request) {
        Client client = Client.builder()
                .identificationType(request.identificationType())
                .identificationNumber(request.identificationNumber())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .birthDate(request.birthDate())
                .build();

        validateAdult(request.birthDate());

        Client saved = clientRepository.save(client);
        return clientMapper.toResponse(saved);
    }

    @Override
    public List<ClientResponse> findAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Override
    public ClientResponse findById(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("No se encontró el cliente con el id: " + id));

        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponse update(UUID id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("No se encontró el cliente con el id: " + id));

        client.setIdentificationType(request.identificationType());
        client.setIdentificationNumber(request.identificationNumber());
        client.setFirstName(request.firstName());
        client.setLastName(request.lastName());
        client.setEmail(request.email());
        client.setBirthDate(request.birthDate());

        Client saved = clientRepository.save(client);
        return clientMapper.toResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("No se encontró el cliente con el id: " + id));

        if (accountRepository.existsByClientId(id)) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene productos vinculados.");
        }

        clientRepository.delete(client);
    }

    private void validateAdult(LocalDate birthDate){
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if(age < 18){
            throw new BusinessException("El cliente debe ser mayor de edad.");
        }
    }
}
