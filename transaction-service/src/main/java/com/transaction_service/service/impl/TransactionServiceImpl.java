package com.transaction_service.service.impl;

import com.transaction_service.dto.*;
import com.transaction_service.entity.Transaction;
import com.transaction_service.enums.*;
import com.transaction_service.exception.BadRequestException;
import com.transaction_service.exception.ForbiddenException;
import com.transaction_service.exception.NotFoundException;
import com.transaction_service.feign.AccountFeignClient;
import com.transaction_service.kafka.dto.BalanceUpdateEvent;
import com.transaction_service.kafka.dto.TransactionCompletedEvent;
import com.transaction_service.kafka.dto.TransactionFailedEvent;
import com.transaction_service.kafka.outbox.OutboxEventService;
import com.transaction_service.kafka.service.TransactionEventPublisher;
import com.transaction_service.repository.TransactionRepository;
import com.transaction_service.service.TransactionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import feign.FeignException;
import com.transaction_service.exception.ServiceUnavailableException;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountFeignClient accountFeignClient;
    private final ModelMapper modelMapper;
    private final TransactionEventPublisher transactionEventPublisher;
    private final OutboxEventService outboxEventService;

    @Override
    public ApiResponse<TransactionDTO> deposit(DepositRequest request, String correlationid) {
        fetchAndValidateAccount(request.getToAccountNumber(),correlationid);

        Transaction deposit = Transaction.builder()
                .reference(generateReference("DEP"))
                .fromAccountNumber("BANK_NOW_VAULT")
                .fromBankCode("BANK NOW")
                .currency(Currency.VND)
                .toAccountNumber(request.getToAccountNumber())
                .toBankCode("BANK NOW")
                .amount(request.getAmount())
                .transactionDirection(TransactionDirection.CREDIT)
                .channel(Channel.API)
                .description(request.getDescription())
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(deposit);

        BalanceUpdateEvent balanceUpdateEvent = BalanceUpdateEvent.builder()
                .accountNumber(request.getToAccountNumber())
                .amount(request.getAmount())
                .currency(Currency.VND)
                .description(request.getDescription())
                .transactionDirection(TransactionDirection.CREDIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .reference(savedTransaction.getReference())
                .build();
        transactionEventPublisher.publishBalanceUpdateRequested(balanceUpdateEvent);

        return new ApiResponse<>(
                201,
                "Deposit Successful",
                modelMapper.map(savedTransaction, TransactionDTO.class));

    }

    @Transactional
    @Override
    public ApiResponse<TransactionDTO> transfer(
            TransferRequest request,
            String correlationId) {

        String reference = generateReference("TRF");

        Transaction transaction = Transaction.builder()
                .reference(reference)
                .fromAccountNumber(request.getFromAccountNumber())
                .fromBankCode("BANK NOW")
                .toAccountNumber(request.getToAccountNumber())
                .toBankCode("BANK NOW")
                .amount(request.getAmount())
                .currency(Currency.VND)
                .channel(Channel.API)
                .description(request.getDescription())
                .transactionType(TransactionType.TRANSFER)
                .transactionStatus(TransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction saved =
                transactionRepository.save(transaction);

        try {
            InternalTransferRequest internalRequest =
                    InternalTransferRequest.builder()
                            .reference(reference)
                            .fromAccountNumber(
                                    request.getFromAccountNumber()
                            )
                            .toAccountNumber(
                                    request.getToAccountNumber()
                            )
                            .amount(request.getAmount())
                            .build();

            ApiResponse<InternalTransferResponse> response =
                    accountFeignClient.transfer(
                            internalRequest,
                            correlationId
                    );

            if (response == null || response.data() == null) {
                throw new ServiceUnavailableException(
                        "User Account Service returned no data"
                );
            }

            saved.setTransactionStatus(
                    TransactionStatus.SUCCESS
            );

            Transaction completed =
                    transactionRepository.save(saved);

            TransactionCompletedEvent event =
                    toTransactionCompletedEvent(
                            completed,
                            response.data(),
                            request,
                            correlationId
                    );

            outboxEventService.saveEvent(
                    completed.getReference(),
                    "banking.transaction.completed",
                    event
            );

            return new ApiResponse<>(
                    200,
                    "Transfer Successful",
                    modelMapper.map(
                            completed,
                            TransactionDTO.class
                    )
            );

        } catch (FeignException exception) {
            markFailed(saved, correlationId, exception);

            if (exception.status() == 400) {
                throw new BadRequestException(
                        "Transfer was rejected by User Account Service"
                );
            }

            if (exception.status() == 403) {
                throw new ForbiddenException(
                        "You are not allowed to transfer from this account"
                );
            }

            if (exception.status() == 404) {
                throw new NotFoundException(
                        "Source or destination account was not found"
                );
            }

            throw new ServiceUnavailableException(
                    "User Account Service is unavailable"
            );

        } catch (RuntimeException exception) {
            markFailed(saved, correlationId, exception);
            transactionEventPublisher.publishTransactionFailed(
                    toTransactionFailedEvent(
                            saved,
                            correlationId,
                            exception
                    )
            );
            throw new ServiceUnavailableException(
                    "Transfer could not be completed"
            );
        }
    }

    @Override
    public ApiResponse<TransactionDTO> withdraw(WithdrawRequest request, String correlationid) {
        AccountDTO account = fetchOwnedAccount(
                request.getFromAccountNumber(),
                correlationid
        );

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Inactive Account");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient Fund");
        }

        Transaction withdrawalTxn = Transaction.builder()
                .reference(generateReference("WDR"))
                .fromAccountNumber(request.getFromAccountNumber())
                .fromBankCode("BANK NOW")
                .currency(Currency.VND)
                .toAccountNumber("BANK_NOW_VAULT")
                .toBankCode("BANK_NOW")
                .amount(request.getAmount())
                .channel(Channel.API)
                .description(request.getDescription())
                .transactionType(TransactionType.WITHDRAWAL)
                .transactionStatus(TransactionStatus.SUCCESS)
                .transactionDirection(TransactionDirection.DEBIT)
                .createdAt(LocalDateTime.now())
                .build();

        Transaction savedWithdrawalTnx = transactionRepository.save(withdrawalTxn);

        transactionEventPublisher.publishBalanceUpdateRequested(BalanceUpdateEvent.builder()
                .accountNumber(request.getFromAccountNumber())
                .amount(request.getAmount())
                .currency(Currency.VND)
                .description(request.getDescription())
                .transactionDirection(TransactionDirection.DEBIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .reference(savedWithdrawalTnx.getReference())
                .build());

        return new ApiResponse<>(
                201,
                "Withdrawal Successful",
                modelMapper.map(savedWithdrawalTnx, TransactionDTO.class)
        );
    }



    @Override
    public ApiResponse<TransactionDTO> getTransactionByReference(String reference) {

        log.info("reference is: {}", reference);

        Transaction txn = transactionRepository.findByReference(reference)
                .orElseThrow(()-> new NotFoundException("Transaction Not Found"));

        TransactionDTO dto = modelMapper.map(txn, TransactionDTO.class);

        return new ApiResponse<>(
                200,
                "Transaction Retrieved",
                dto
        );

    }

    @Override
    public ApiResponse<List<TransactionDTO>> getAllTransactionHistoryOfAnAccountNumber(String accountNumber) {

        List<Transaction> transactionList = transactionRepository.findAllByAccountNumber(accountNumber);

        log.info("transaction history count is {}", (long) transactionList.size());

        List<TransactionDTO> transactionDTOS = transactionList.stream().map(t-> modelMapper.map(t, TransactionDTO.class)).toList();

        return new ApiResponse<>(
                200,
                "Transaction History Retrieved for the Account",
                transactionDTOS
        );

    }

    @Override
    public ApiResponse<List<TransactionDTO>> getTransactionHistory(String accountNumber, LocalDateTime start, LocalDateTime end) {

        List<Transaction> history = transactionRepository.findAllAccountNumberAndDateRange(accountNumber, start, end);

        List<TransactionDTO> transactionDTOS = history.stream().map(t-> modelMapper.map(t, TransactionDTO.class)).toList();

        return new ApiResponse<>(
                200,
                "Transaction History Retrieved for the Account",
                transactionDTOS
        );
    }

    @Override
    public ApiResponse<List<TransactionDTO>> getMyTransactionHistoryByDirection(String accountNumber, TransactionDirection direction) {

        List<Transaction> transactions = direction.equals(TransactionDirection.DEBIT) ?
                transactionRepository.findByFromAccountNumber(accountNumber) :
                transactionRepository.findByToAccountNumber(accountNumber);


        List<TransactionDTO> transactionDTOS = transactions.stream().map(t-> modelMapper.map(t, TransactionDTO.class)).toList();

        return new ApiResponse<>(
                200,
                "Transaction History Retrieved by direction for the Account",
                transactionDTOS
        );

    }


    private AccountDTO fetchAndValidateAccount(String accountNumber,String correlationId) {

        ApiResponse<AccountDTO> response =
                accountFeignClient.getAccountByNumber(
                        accountNumber,
                        correlationId
                ); 

        if (response == null || response.data() == null) {
            throw new NotFoundException("Account " + accountNumber + "not found");
        }

        AccountDTO account = response.data();

        if (account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            throw new BadRequestException("Transaction Denied: Account is Closed");
        }

        return account;

    }

    private AccountDTO fetchOwnedAccount(
            String accountNumber,
            String correlationId) {

        AccountDTO account =
                fetchAndValidateAccount(accountNumber, correlationId);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenException("Authentication is required");
        }

        String loggedInEmail = authentication.getName();

        if (!account.getOwnerEmail().equalsIgnoreCase(loggedInEmail)) {
            throw new ForbiddenException(
                    "You are not the owner of account " + accountNumber);
        }

        return account;
    }

    private String generateReference(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

    private void markFailed(
            Transaction transaction,
            String correlationId,
            Exception exception) {

        transaction.setTransactionStatus(
                TransactionStatus.FAILED
        );

        transactionRepository.save(transaction);

        log.error(
                "Transfer failed. reference={}, correlationId={}",
                transaction.getReference(),
                correlationId,
                exception
        );
    }




    private TransactionCompletedEvent toTransactionCompletedEvent(
            Transaction transaction,
            InternalTransferResponse result,
            TransferRequest request,
            String correlationId
    ) {
        AccountBalanceSnapshot debitAccount = result.getDebitAccount();
        AccountBalanceSnapshot creditAccount = result.getCreditAccount();

        return TransactionCompletedEvent.builder()

                // Event metadata
                .eventId(UUID.randomUUID())
                .eventType("banking.transaction.completed")
                .eventVersion(1)
                .occurredAt(Instant.now())
                .correlationId(correlationId)
                .aggregateId(transaction.getReference())

                // Business data
                .transactionReference(transaction.getReference())
                .transactionType(transaction.getTransactionType().name())

                .fromAccountNumber(debitAccount.getAccountNumber())
                .fromEmail(debitAccount.getEmail())
                .fromFirstName(debitAccount.getFirstName())
                .fromCurrentBalance(debitAccount.getCurrentBalance())

                .toAccountNumber(creditAccount.getAccountNumber())
                .toEmail(creditAccount.getEmail())
                .toFirstName(creditAccount.getFirstName())
                .toCurrentBalance(creditAccount.getCurrentBalance())

                .amount(transaction.getAmount())
                .currency(transaction.getCurrency().name())
                .status(transaction.getTransactionStatus().name())
                .description(request.getDescription())

                .build();
    }

    private TransactionFailedEvent toTransactionFailedEvent(
            Transaction transaction,
            String correlationId,
            RuntimeException exception) {

        return TransactionFailedEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("banking.transaction.failed")
                .eventVersion(1)
                .occurredAt(Instant.now())
                .correlationId(correlationId)
                .aggregateId(transaction.getReference())

                .transactionReference(transaction.getReference())
                .transactionType(transaction.getTransactionType().name())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency().name())
                .status(TransactionStatus.FAILED.name())
                .failureReason(exception.getClass().getSimpleName())
                .build();
    }
}
