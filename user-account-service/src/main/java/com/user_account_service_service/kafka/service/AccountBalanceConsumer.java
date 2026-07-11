package com.user_account_service_service.kafka.service;

import com.user_account_service_service.entity.Account;
import com.user_account_service_service.enums.transaction.TransactionDirection;
import com.user_account_service_service.exceptions.NotFoundException;
import com.user_account_service_service.kafka.dto.BalanceUpdateEvent;
import com.user_account_service_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountBalanceConsumer {

    private final AccountRepository accountRepository;
    private final AccountEventPublisher eventPublisher;

    @KafkaListener(topics = "balance-update-events", groupId = "account-group")
    @Transactional
    public void consumerBalanceUpdate(BalanceUpdateEvent event){
        log.info("processing balance update for account number: {}", event.getAccountNumber());

        Account account = accountRepository.findByAccountNumber(event.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("account Number nor found"));

        if(event.getTransactionDirection() == TransactionDirection.CREDIT){
            account.setBalance(account.getBalance().add(event.getAmount()));
        } else if (event.getTransactionDirection() == TransactionDirection.DEBIT) {
            account.setBalance(account.getBalance().subtract(event.getAmount()));
        }

        accountRepository.save(account);

        BalanceUpdateEvent balanceUpdateEventToPublishToNotification = BalanceUpdateEvent.builder()
                .email(account.getUser().getEmail())
                .firstName(account.getUser().getFirstName())
                .accountNumber(account.getAccountNumber())
                .amount(event.getAmount())
                .transactionDirection(event.getTransactionDirection())
                .reference(event.getReference())
                .description(event.getDescription())
                .currentBalance(account.getBalance())
                .build();

        eventPublisher.publishTransactionNotificationEvent(balanceUpdateEventToPublishToNotification);
    }
}
