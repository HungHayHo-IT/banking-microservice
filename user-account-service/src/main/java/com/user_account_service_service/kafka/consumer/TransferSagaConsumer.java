package com.user_account_service_service.kafka.consumer;

import com.user_account_service_service.kafka.dto.TransferSagaEvent;
import com.user_account_service_service.service.AccountsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class TransferSagaConsumer {
    private final AccountsService accountService;

    @KafkaListener(
            topics = "${app.kafka.topics.transfer-debit-requested}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleDebitRequested(TransferSagaEvent event) {

        log.info(
                "Received TRANSFER_DEBIT_REQUESTED event. " +
                        "eventId={}, transactionReference={}, fromAccount={}, toAccount={}, amount={}, correlationId={}",
                event.getEventId(),
                event.getTransactionReference(),
                event.getFromAccountNumber(),
                event.getToAccountNumber(),
                event.getAmount(),
                event.getCorrelationId()
        );
        accountService.handleTransferDebit(event);
    }
}