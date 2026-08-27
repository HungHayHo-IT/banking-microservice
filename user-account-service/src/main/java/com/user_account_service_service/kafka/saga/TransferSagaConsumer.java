package com.user_account_service_service.kafka.saga;

import com.user_account_service_service.kafka.dto.saga.DebitRequestedEvent;
import com.user_account_service_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferSagaConsumer {

    private final AccountRepository accountRepository;

    private final TransferSagaEventPublisher eventPublisher;

    @KafkaListener(
            topics = "banking.transaction.debit.requested",
            groupId = "user-account-service-saga"
    )
    @Transactional
    public void handleDebit(
            DebitRequestedEvent event
    ) {

        log.info(
                "Received debit request. reference={}",
                event.getReference()
        );

        // xử lý ở đây
    }
}