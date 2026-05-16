package com.example.prueba_tecnica.account.service;

import com.example.prueba_tecnica.account.dto.request.AccountRequest;
import com.example.prueba_tecnica.account.dto.response.AccountResponse;
import com.example.prueba_tecnica.account.entity.Account;
import com.example.prueba_tecnica.account.mapper.AccountMapper;
import com.example.prueba_tecnica.account.repository.AccountRepository;
import com.example.prueba_tecnica.client.entity.Client;
import com.example.prueba_tecnica.client.repository.ClientRepository;
import com.example.prueba_tecnica.common.enums.AccountStatus;
import com.example.prueba_tecnica.common.enums.AccountType;
import com.example.prueba_tecnica.exception.AccountNotFoundException;
import com.example.prueba_tecnica.exception.BusinessException;
import com.example.prueba_tecnica.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountResponse create(AccountRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ClientNotFoundException(
                        "No se encontró el cliente con el id: " + request.clientId()
                ));

        validateInitialBalance(request.type(), request.initialBalance());

        Account account = Account.builder()
                .type(request.type())
                .accountNumber(generateAccountNumber(request.type()))
                .status(AccountStatus.ACTIVE)
                .balance(request.initialBalance())
                .gmfExempt(request.gmfExempt())
                .client(client)
                .build();

        Account saved = accountRepository.save(account);
        return accountMapper.toResponse(saved);
    }

    @Override
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    public AccountResponse findById(UUID id) {
        Account account = getAccountById(id);
        return accountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponse> findByClientId(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(
                        "No se encontró el cliente con el id: " + clientId
                ));

        return accountRepository.findByClientId(clientId).stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    public AccountResponse activate(UUID id) {
        Account account = changeStatus(id, AccountStatus.ACTIVE);
        Account saved = accountRepository.save(account);
        return accountMapper.toResponse(saved);
    }

    @Override
    public AccountResponse inactivate(UUID id) {
        Account account = changeStatus(id, AccountStatus.INACTIVE);
        Account saved = accountRepository.save(account);
        return accountMapper.toResponse(saved);
    }

    @Override
    public AccountResponse cancel(UUID id) {
        Account account = changeStatus(id, AccountStatus.CANCELLED);
        Account saved = accountRepository.save(account);
        return accountMapper.toResponse(saved);
    }




    private Account changeStatus(UUID id, AccountStatus status){
        Account account = getAccountById(id);

        validateCancellation(account, status);

        account.setStatus(status);

        return account;
    }

    private void validateCancellation(Account account, AccountStatus status) {
        if (status != AccountStatus.CANCELLED) {
            return;
        }

        if (account.getStatus() == AccountStatus.CANCELLED) {
            throw new BusinessException("La cuenta ya se encuentra cancelada.");
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("Solo se pueden cancelar cuentas con saldo igual a cero.");
        }
    }

    private void validateInitialBalance(AccountType type, BigDecimal balance) {
        if (type == AccountType.SAVINGS && balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("La cuenta de ahorros no puede tener saldo menor a cero.");
        }
    }

    private Account getAccountById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(
                        "No se encontró la cuenta con el id: " + id
                ));
    }

    private String generateAccountNumber(AccountType type) {
        String prefix = type == AccountType.SAVINGS ? "53" : "33";

        String accountNumber;

        do {
            String suffix = String.format("%08d", (int) (Math.random() * 100_000_000));
            accountNumber = prefix + suffix;
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
