package com.user_account_service_service.kafka.saga;

import com.user_account_service_service.kafka.dto.saga.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferSagaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDebitCompleted(
            DebitCompletedEvent event
    ) {

        kafkaTemplate.send(
                "banking.transaction.debit.completed",
                event.getReference(),
                event
        );
    }

    public void publishDebitFailed(
            DebitFailedEvent event
    ) {

        kafkaTemplate.send(
                "banking.transaction.debit.failed",
                event.getReference(),
                event
        );
    }

    public void publishCreditCompleted(
            CreditCompletedEvent event
    ) {

        kafkaTemplate.send(
                "banking.transaction.credit.completed",
                event.getReference(),
                event
        );
    }

    public void publishCreditFailed(
            CreditFailedEvent event
    ) {

        kafkaTemplate.send(
                "banking.transaction.credit.failed",
                event.getReference(),
                event
        );
    }

    public void publishRefundCompleted(
            RefundCompletedEvent event
    ) {

        kafkaTemplate.send(
                "banking.transaction.refund.completed",
                event.getReference(),
                event
        );
    }
}