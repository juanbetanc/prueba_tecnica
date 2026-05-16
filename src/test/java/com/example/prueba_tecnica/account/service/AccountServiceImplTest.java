package com.example.prueba_tecnica.account.service;

import com.example.prueba_tecnica.account.dto.request.AccountRequest;
import com.example.prueba_tecnica.account.dto.response.AccountResponse;
import com.example.prueba_tecnica.account.entity.Account;
import com.example.prueba_tecnica.account.mapper.AccountMapper;
import com.example.prueba_tecnica.account.repository.AccountRepository;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.client.entity.Client;
import com.example.prueba_tecnica.client.repository.ClientRepository;
import com.example.prueba_tecnica.common.enums.AccountStatus;
import com.example.prueba_tecnica.common.enums.AccountType;
import com.example.prueba_tecnica.common.enums.IdentificationType;
import com.example.prueba_tecnica.exception.AccountNotFoundException;
import com.example.prueba_tecnica.exception.BusinessException;
import com.example.prueba_tecnica.exception.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    private UUID clientId;
    private UUID accountId;
    private Client client;
    private Account account;
    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        accountId = UUID.randomUUID();

        client = Client.builder()
                .id(clientId)
                .identificationType(IdentificationType.CC)
                .identificationNumber("123456789")
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@test.com")
                .birthDate(LocalDate.of(1995, 1, 1))
                .build();

        account = Account.builder()
                .id(accountId)
                .type(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .gmfExempt(false)
                .client(client)
                .build();

        ClientResponse clientResponse = new ClientResponse(
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

        accountResponse = new AccountResponse(
                accountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.ACTIVE,
                BigDecimal.ZERO,
                false,
                LocalDateTime.now(),
                null,
                clientResponse
        );
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        AccountRequest request = new AccountRequest(
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                false,
                clientId
        );

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toResponse(account)).thenReturn(accountResponse);

        AccountResponse result = accountService.create(request);

        assertNotNull(result);
        assertEquals(accountId, result.id());
        assertEquals(AccountType.SAVINGS, result.type());
        assertEquals(AccountStatus.ACTIVE, result.status());
        assertEquals(BigDecimal.ZERO, result.balance());

        verify(clientRepository).findById(clientId);
        verify(accountRepository).save(any(Account.class));
        verify(accountMapper).toResponse(account);
    }

    @Test
    void shouldThrowExceptionWhenClientDoesNotExist() {
        AccountRequest request = new AccountRequest(
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                false,
                clientId
        );

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> accountService.create(request)
        );

        assertEquals("No se encontró el cliente con el id: " + clientId, exception.getMessage());

        verify(clientRepository).findById(clientId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenSavingsAccountHasNegativeInitialBalance() {
        AccountRequest request = new AccountRequest(
                AccountType.SAVINGS,
                new BigDecimal("-1000"),
                false,
                clientId
        );

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.create(request)
        );

        assertEquals("La cuenta de ahorros no puede tener saldo menor a cero.", exception.getMessage());

        verify(clientRepository).findById(clientId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldFindAccountByIdSuccessfully() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toResponse(account)).thenReturn(accountResponse);

        AccountResponse result = accountService.findById(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.id());
        assertEquals("5312345678", result.accountNumber());

        verify(accountRepository).findById(accountId);
        verify(accountMapper).toResponse(account);
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> accountService.findById(accountId)
        );

        assertEquals("No se encontró la cuenta con el id: " + accountId, exception.getMessage());

        verify(accountRepository).findById(accountId);
        verify(accountMapper, never()).toResponse(any(Account.class));
    }

    @Test
    void shouldFindAllAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of(account));
        when(accountMapper.toResponse(account)).thenReturn(accountResponse);

        List<AccountResponse> result = accountService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(accountId, result.get(0).id());

        verify(accountRepository).findAll();
        verify(accountMapper).toResponse(account);
    }

    @Test
    void shouldActivateAccount() {
        account.setStatus(AccountStatus.INACTIVE);

        Account activatedAccount = Account.builder()
                .id(accountId)
                .type(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .gmfExempt(false)
                .client(client)
                .build();

        AccountResponse activatedResponse = new AccountResponse(
                accountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.ACTIVE,
                BigDecimal.ZERO,
                false,
                LocalDateTime.now(),
                null,
                accountResponse.client()
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(activatedAccount);
        when(accountMapper.toResponse(activatedAccount)).thenReturn(activatedResponse);

        AccountResponse result = accountService.activate(accountId);

        assertNotNull(result);
        assertEquals(AccountStatus.ACTIVE, result.status());

        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldInactivateAccount() {
        Account inactiveAccount = Account.builder()
                .id(accountId)
                .type(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.INACTIVE)
                .balance(BigDecimal.ZERO)
                .gmfExempt(false)
                .client(client)
                .build();

        AccountResponse inactiveResponse = new AccountResponse(
                accountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.INACTIVE,
                BigDecimal.ZERO,
                false,
                LocalDateTime.now(),
                null,
                accountResponse.client()
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(inactiveAccount);
        when(accountMapper.toResponse(inactiveAccount)).thenReturn(inactiveResponse);

        AccountResponse result = accountService.inactivate(accountId);

        assertNotNull(result);
        assertEquals(AccountStatus.INACTIVE, result.status());

        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldCancelAccountWhenBalanceIsZero() {
        Account cancelledAccount = Account.builder()
                .id(accountId)
                .type(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.CANCELLED)
                .balance(BigDecimal.ZERO)
                .gmfExempt(false)
                .client(client)
                .build();

        AccountResponse cancelledResponse = new AccountResponse(
                accountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.CANCELLED,
                BigDecimal.ZERO,
                false,
                LocalDateTime.now(),
                null,
                accountResponse.client()
        );

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(cancelledAccount);
        when(accountMapper.toResponse(cancelledAccount)).thenReturn(cancelledResponse);

        AccountResponse result = accountService.cancel(accountId);

        assertNotNull(result);
        assertEquals(AccountStatus.CANCELLED, result.status());

        verify(accountRepository).findById(accountId);
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenCancellingAccountWithBalanceDifferentFromZero() {
        account.setBalance(new BigDecimal("50000"));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> accountService.cancel(accountId)
        );

        assertEquals("Solo se pueden cancelar cuentas con saldo igual a cero.", exception.getMessage());

        verify(accountRepository).findById(accountId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldFindAccountsByClientId() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findByClientId(clientId)).thenReturn(List.of(account));
        when(accountMapper.toResponse(account)).thenReturn(accountResponse);

        List<AccountResponse> result = accountService.findByClientId(clientId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(accountId, result.get(0).id());

        verify(clientRepository).findById(clientId);
        verify(accountRepository).findByClientId(clientId);
        verify(accountMapper).toResponse(account);
    }

    @Test
    void shouldThrowExceptionWhenFindingAccountsByNonExistingClient() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        ClientNotFoundException exception = assertThrows(
                ClientNotFoundException.class,
                () -> accountService.findByClientId(clientId)
        );

        assertEquals("No se encontró el cliente con el id: " + clientId, exception.getMessage());

        verify(clientRepository).findById(clientId);
        verify(accountRepository, never()).findByClientId(any(UUID.class));
    }
}