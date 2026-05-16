package com.example.prueba_tecnica.transaction.service;

import com.example.prueba_tecnica.account.entity.Account;
import com.example.prueba_tecnica.account.repository.AccountRepository;
import com.example.prueba_tecnica.common.enums.AccountStatus;
import com.example.prueba_tecnica.common.enums.AccountType;
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

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransacionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        Account account = getAccount(request.accountId());

        validateAccountCanOperate(account);

        account.setBalance(account.getBalance().add(request.amount()));

        FinancialTransaction transaction = FinancialTransaction.builder()
                .type(TransactionType.DEPOSIT)
                .amount(request.amount())
                .sourceAccount(account)
                .balanceAfterTransaction(account.getBalance())
                .build();

        accountRepository.save(account);
        FinancialTransaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {
        Account account = getAccount(request.accountId());

        validateAccountCanOperate(account);

        BigDecimal newBalance = account.getBalance().subtract(request.amount());

        validateBalanceByAccountType(account, newBalance);

        account.setBalance(newBalance);

        FinancialTransaction transaction = FinancialTransaction.builder()
                .type(TransactionType.WITHDRAWAL)
                .amount(request.amount())
                .sourceAccount(account)
                .balanceAfterTransaction(account.getBalance())
                .build();

        accountRepository.save(account);

        FinancialTransaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    @Transactional
    public List<TransactionResponse> transfer(TransferRequest request) {
        // Valida que las dos cuentas no sean la misma
        if(request.sourceAccountId().equals(request.targetAccountId())){
            throw new BusinessException("La cuenta origen y destino no pueden ser la misma.");
        }
        // Se obtienen las cuentas
        Account sourceAccount = getAccount(request.sourceAccountId());
        Account targetAccount = getAccount(request.targetAccountId());

        // Se valida que ambas cuentas puedan operar
        validateAccountCanOperate(sourceAccount);
        validateAccountCanOperate(targetAccount);

        // Se calculan los saldos finales
        BigDecimal sourceNewBalance = sourceAccount.getBalance().subtract(request.amount());
        BigDecimal targetNewBalance = targetAccount.getBalance().add(request.amount());

        // Se valida el tipo de la cuenta
        // para evitar que su saldo final sea 0 en caso de que sea una cuenta de ahorros
        validateBalanceByAccountType(sourceAccount, sourceNewBalance);

        sourceAccount.setBalance(sourceNewBalance);
        targetAccount.setBalance(targetNewBalance);

        FinancialTransaction debit = FinancialTransaction.builder()
                .type(TransactionType.TRANSFER_DEBIT)
                .amount(request.amount())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .balanceAfterTransaction(sourceNewBalance)
                .build();

        FinancialTransaction credit = FinancialTransaction.builder()
                .type(TransactionType.TRANSFER_CREDIT)
                .amount(request.amount())
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .balanceAfterTransaction(targetNewBalance)
                .build();

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        List<FinancialTransaction> transactions = transactionRepository.saveAll(List.of(debit, credit));

        return transactions.stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    @Override
    public List<TransactionResponse> getByAccount(UUID id) {
        Account account = getAccount(id);
        return transactionRepository.findBySourceAccount(account)
                .stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    private Account getAccount(UUID accountId){
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Cuenta no encontrada"));
    }

    private void validateAccountCanOperate(Account account) {
        if (account.getStatus() == AccountStatus.CANCELLED) {
            throw new BusinessException("La cuenta está cancelada.");
        }

        if (account.getStatus() == AccountStatus.INACTIVE) {
            throw new BusinessException("La cuenta está inactiva.");
        }
    }

    private void validateBalanceByAccountType(Account account, BigDecimal newBalance){
        if (account.getType() == AccountType.SAVINGS && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("La cuenta de ahorros no puede quedar con saldo negativo.");
        }
    }
}
