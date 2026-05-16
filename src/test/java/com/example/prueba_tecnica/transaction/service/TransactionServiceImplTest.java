package com.example.prueba_tecnica.transaction.service;

import com.example.prueba_tecnica.account.dto.response.AccountResponse;
import com.example.prueba_tecnica.account.entity.Account;
import com.example.prueba_tecnica.account.repository.AccountRepository;
import com.example.prueba_tecnica.client.dto.response.ClientResponse;
import com.example.prueba_tecnica.client.entity.Client;
import com.example.prueba_tecnica.common.enums.AccountStatus;
import com.example.prueba_tecnica.common.enums.AccountType;
import com.example.prueba_tecnica.common.enums.IdentificationType;
import com.example.prueba_tecnica.common.enums.TransactionType;
import com.example.prueba_tecnica.exception.AccountNotFoundException;
import com.example.prueba_tecnica.exception.BusinessException;
import com.example.prueba_tecnica.transaction.dto.request.DepositRequest;
import com.example.prueba_tecnica.transaction.dto.request.TransferRequest;
import com.example.prueba_tecnica.transaction.dto.request.WithdrawRequest;
import com.example.prueba_tecnica.transaction.dto.response.TransactionResponse;
import com.example.prueba_tecnica.transaction.entity.FinancialTransaction;
import com.example.prueba_tecnica.transaction.mapper.TransactionMapper;
import com.example.prueba_tecnica.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransacionServiceImpl transactionService;

    private UUID clientId;
    private UUID sourceAccountId;
    private UUID targetAccountId;

    private Client client;

    private Account sourceAccount;
    private Account targetAccount;

    private ClientResponse clientResponse;
    private AccountResponse sourceAccountResponse;
    private AccountResponse targetAccountResponse;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        sourceAccountId = UUID.randomUUID();
        targetAccountId = UUID.randomUUID();

        client = Client.builder()
                .id(clientId)
                .identificationType(IdentificationType.CC)
                .identificationNumber("123456789")
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@test.com")
                .birthDate(LocalDate.of(1995, 1, 1))
                .build();

        sourceAccount = Account.builder()
                .id(sourceAccountId)
                .type(AccountType.SAVINGS)
                .accountNumber("5312345678")
                .status(AccountStatus.ACTIVE)
                .balance(new BigDecimal("100000"))
                .gmfExempt(false)
                .client(client)
                .build();

        targetAccount = Account.builder()
                .id(targetAccountId)
                .type(AccountType.SAVINGS)
                .accountNumber("5398765432")
                .status(AccountStatus.ACTIVE)
                .balance(new BigDecimal("50000"))
                .gmfExempt(false)
                .client(client)
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

        sourceAccountResponse = new AccountResponse(
                sourceAccountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.ACTIVE,
                new BigDecimal("100000"),
                false,
                LocalDateTime.now(),
                null,
                clientResponse
        );

        targetAccountResponse = new AccountResponse(
                targetAccountId,
                AccountType.SAVINGS,
                "5398765432",
                AccountStatus.ACTIVE,
                new BigDecimal("50000"),
                false,
                LocalDateTime.now(),
                null,
                clientResponse
        );
    }

    @Test
    void shouldDepositSuccessfully() {
        DepositRequest request = new DepositRequest(
                sourceAccountId,
                new BigDecimal("20000")
        );

        FinancialTransaction transaction = FinancialTransaction.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.DEPOSIT)
                .amount(request.amount())
                .sourceAccount(sourceAccount)
                .targetAccount(null)
                .balanceAfterTransaction(new BigDecimal("120000"))
                .build();

        AccountResponse updatedSourceAccountResponse = new AccountResponse(
                sourceAccountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.ACTIVE,
                new BigDecimal("120000"),
                false,
                LocalDateTime.now(),
                null,
                clientResponse
        );

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                TransactionType.DEPOSIT,
                new BigDecimal("20000"),
                new BigDecimal("120000"),
                updatedSourceAccountResponse,
                null,
                LocalDateTime.now()
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount);
        when(transactionRepository.save(any(FinancialTransaction.class))).thenReturn(transaction);
        when(transactionMapper.toResponse(transaction)).thenReturn(response);

        TransactionResponse result = transactionService.deposit(request);

        assertNotNull(result);
        assertEquals(TransactionType.DEPOSIT, result.type());
        assertEquals(new BigDecimal("120000"), sourceAccount.getBalance());
        assertEquals(sourceAccountId, result.sourceAccount().id());
        assertNull(result.targetAccount());

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository).save(sourceAccount);
        verify(transactionRepository).save(any(FinancialTransaction.class));
    }

    @Test
    void shouldThrowExceptionWhenDepositingToNonExistingAccount() {
        DepositRequest request = new DepositRequest(
                sourceAccountId,
                new BigDecimal("20000")
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.empty());

        AccountNotFoundException exception = assertThrows(
                AccountNotFoundException.class,
                () -> transactionService.deposit(request)
        );

        assertEquals("Cuenta no encontrada", exception.getMessage());

        verify(accountRepository).findById(sourceAccountId);
        verify(transactionRepository, never()).save(any(FinancialTransaction.class));
    }

    @Test
    void shouldWithdrawSuccessfully() {
        WithdrawRequest request = new WithdrawRequest(
                sourceAccountId,
                new BigDecimal("30000")
        );

        FinancialTransaction transaction = FinancialTransaction.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.WITHDRAWAL)
                .amount(request.amount())
                .sourceAccount(sourceAccount)
                .targetAccount(null)
                .balanceAfterTransaction(new BigDecimal("70000"))
                .build();

        AccountResponse updatedSourceAccountResponse = new AccountResponse(
                sourceAccountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.ACTIVE,
                new BigDecimal("70000"),
                false,
                LocalDateTime.now(),
                null,
                clientResponse
        );

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                TransactionType.WITHDRAWAL,
                new BigDecimal("30000"),
                new BigDecimal("70000"),
                updatedSourceAccountResponse,
                null,
                LocalDateTime.now()
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(sourceAccount);
        when(transactionRepository.save(any(FinancialTransaction.class))).thenReturn(transaction);
        when(transactionMapper.toResponse(transaction)).thenReturn(response);

        TransactionResponse result = transactionService.withdraw(request);

        assertNotNull(result);
        assertEquals(TransactionType.WITHDRAWAL, result.type());
        assertEquals(new BigDecimal("70000"), sourceAccount.getBalance());
        assertEquals(sourceAccountId, result.sourceAccount().id());
        assertNull(result.targetAccount());

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository).save(sourceAccount);
        verify(transactionRepository).save(any(FinancialTransaction.class));
    }

    @Test
    void shouldThrowExceptionWhenSavingsWithdrawalLeavesNegativeBalance() {
        WithdrawRequest request = new WithdrawRequest(
                sourceAccountId,
                new BigDecimal("150000")
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.withdraw(request)
        );

        assertEquals("La cuenta de ahorros no puede quedar con saldo negativo.", exception.getMessage());

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(FinancialTransaction.class));
    }

    @Test
    void shouldThrowExceptionWhenAccountIsInactive() {
        sourceAccount.setStatus(AccountStatus.INACTIVE);

        DepositRequest request = new DepositRequest(
                sourceAccountId,
                new BigDecimal("20000")
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.deposit(request)
        );

        assertEquals("La cuenta está inactiva.", exception.getMessage());

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldThrowExceptionWhenAccountIsCancelled() {
        sourceAccount.setStatus(AccountStatus.CANCELLED);

        DepositRequest request = new DepositRequest(
                sourceAccountId,
                new BigDecimal("20000")
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.deposit(request)
        );

        assertEquals("La cuenta está cancelada.", exception.getMessage());

        verify(accountRepository).findById(sourceAccountId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldTransferSuccessfully() {
        TransferRequest request = new TransferRequest(
                sourceAccountId,
                targetAccountId,
                new BigDecimal("40000")
        );

        FinancialTransaction debit = FinancialTransaction.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.TRANSFER_DEBIT)
                .amount(request.amount())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .balanceAfterTransaction(new BigDecimal("60000"))
                .build();

        FinancialTransaction credit = FinancialTransaction.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.TRANSFER_CREDIT)
                .amount(request.amount())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .balanceAfterTransaction(new BigDecimal("90000"))
                .build();

        AccountResponse updatedSourceAccountResponse = new AccountResponse(
                sourceAccountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.ACTIVE,
                new BigDecimal("60000"),
                false,
                LocalDateTime.now(),
                null,
                clientResponse
        );

        AccountResponse updatedTargetAccountResponse = new AccountResponse(
                targetAccountId,
                AccountType.SAVINGS,
                "5398765432",
                AccountStatus.ACTIVE,
                new BigDecimal("90000"),
                false,
                LocalDateTime.now(),
                null,
                clientResponse
        );

        TransactionResponse debitResponse = new TransactionResponse(
                debit.getId(),
                TransactionType.TRANSFER_DEBIT,
                new BigDecimal("40000"),
                new BigDecimal("60000"),
                updatedSourceAccountResponse,
                updatedTargetAccountResponse,
                LocalDateTime.now()
        );

        TransactionResponse creditResponse = new TransactionResponse(
                credit.getId(),
                TransactionType.TRANSFER_CREDIT,
                new BigDecimal("40000"),
                new BigDecimal("90000"),
                updatedSourceAccountResponse,
                updatedTargetAccountResponse,
                LocalDateTime.now()
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));
        when(accountRepository.save(sourceAccount)).thenReturn(sourceAccount);
        when(accountRepository.save(targetAccount)).thenReturn(targetAccount);
        when(transactionRepository.saveAll(anyList())).thenReturn(List.of(debit, credit));
        when(transactionMapper.toResponse(debit)).thenReturn(debitResponse);
        when(transactionMapper.toResponse(credit)).thenReturn(creditResponse);

        List<TransactionResponse> result = transactionService.transfer(request);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(new BigDecimal("60000"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("90000"), targetAccount.getBalance());

        assertEquals(TransactionType.TRANSFER_DEBIT, result.get(0).type());
        assertEquals(TransactionType.TRANSFER_CREDIT, result.get(1).type());

        assertEquals(sourceAccountId, result.get(0).sourceAccount().id());
        assertEquals(targetAccountId, result.get(0).targetAccount().id());

        verify(accountRepository).save(sourceAccount);
        verify(accountRepository).save(targetAccount);
        verify(transactionRepository).saveAll(anyList());
    }

    @Test
    void shouldCreateDebitAndCreditMovementsWhenTransferIsSuccessful() {
        TransferRequest request = new TransferRequest(
                sourceAccountId,
                targetAccountId,
                new BigDecimal("40000")
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FinancialTransaction debit = FinancialTransaction.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.TRANSFER_DEBIT)
                .amount(request.amount())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .balanceAfterTransaction(new BigDecimal("60000"))
                .build();

        FinancialTransaction credit = FinancialTransaction.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.TRANSFER_CREDIT)
                .amount(request.amount())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .balanceAfterTransaction(new BigDecimal("90000"))
                .build();

        when(transactionRepository.saveAll(anyList())).thenReturn(List.of(debit, credit));

        when(transactionMapper.toResponse(any(FinancialTransaction.class)))
                .thenAnswer(invocation -> {
                    FinancialTransaction transaction = invocation.getArgument(0);

                    AccountResponse sourceResponseAfterTransfer = new AccountResponse(
                            sourceAccountId,
                            AccountType.SAVINGS,
                            "5312345678",
                            AccountStatus.ACTIVE,
                            sourceAccount.getBalance(),
                            false,
                            LocalDateTime.now(),
                            null,
                            clientResponse
                    );

                    AccountResponse targetResponseAfterTransfer = new AccountResponse(
                            targetAccountId,
                            AccountType.SAVINGS,
                            "5398765432",
                            AccountStatus.ACTIVE,
                            targetAccount.getBalance(),
                            false,
                            LocalDateTime.now(),
                            null,
                            clientResponse
                    );

                    return new TransactionResponse(
                            transaction.getId(),
                            transaction.getType(),
                            transaction.getAmount(),
                            transaction.getBalanceAfterTransaction(),
                            sourceResponseAfterTransfer,
                            targetResponseAfterTransfer,
                            LocalDateTime.now()
                    );
                });

        transactionService.transfer(request);

        ArgumentCaptor<List<FinancialTransaction>> captor = ArgumentCaptor.forClass(List.class);

        verify(transactionRepository).saveAll(captor.capture());

        List<FinancialTransaction> savedTransactions = captor.getValue();

        assertEquals(2, savedTransactions.size());
        assertEquals(TransactionType.TRANSFER_DEBIT, savedTransactions.get(0).getType());
        assertEquals(TransactionType.TRANSFER_CREDIT, savedTransactions.get(1).getType());
    }

    @Test
    void shouldThrowExceptionWhenTransferUsesSameAccount() {
        TransferRequest request = new TransferRequest(
                sourceAccountId,
                sourceAccountId,
                new BigDecimal("40000")
        );

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.transfer(request)
        );

        assertEquals("La cuenta origen y destino no pueden ser la misma.", exception.getMessage());

        verify(accountRepository, never()).findById(any(UUID.class));
        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldThrowExceptionWhenTransferLeavesSavingsAccountNegative() {
        TransferRequest request = new TransferRequest(
                sourceAccountId,
                targetAccountId,
                new BigDecimal("150000")
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccountId)).thenReturn(Optional.of(targetAccount));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> transactionService.transfer(request)
        );

        assertEquals("La cuenta de ahorros no puede quedar con saldo negativo.", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldFindTransactionsByAccountId() {
        FinancialTransaction transaction = FinancialTransaction.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("20000"))
                .sourceAccount(sourceAccount)
                .targetAccount(null)
                .balanceAfterTransaction(new BigDecimal("120000"))
                .build();

        AccountResponse updatedSourceAccountResponse = new AccountResponse(
                sourceAccountId,
                AccountType.SAVINGS,
                "5312345678",
                AccountStatus.ACTIVE,
                new BigDecimal("120000"),
                false,
                LocalDateTime.now(),
                null,
                clientResponse
        );

        TransactionResponse response = new TransactionResponse(
                transaction.getId(),
                TransactionType.DEPOSIT,
                new BigDecimal("20000"),
                new BigDecimal("120000"),
                updatedSourceAccountResponse,
                null,
                LocalDateTime.now()
        );

        when(accountRepository.findById(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(transactionRepository.findBySourceAccount(sourceAccount))
                .thenReturn(List.of(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(response);

        List<TransactionResponse> result = transactionService.getByAccount(sourceAccountId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TransactionType.DEPOSIT, result.get(0).type());
        assertEquals(sourceAccountId, result.get(0).sourceAccount().id());
        assertNull(result.get(0).targetAccount());

        verify(accountRepository).findById(sourceAccountId);
        verify(transactionRepository).findBySourceAccount(sourceAccount);
    }
}