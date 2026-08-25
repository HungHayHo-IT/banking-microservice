package com.notification_service.kafka.service;

import com.notification_service.entity.ProcessedEvent;
import com.notification_service.enums.transaction.Currency;
import com.notification_service.enums.transaction.TransactionDirection;
import com.notification_service.enums.transaction.TransactionStatus;
import com.notification_service.kafka.dto.BalanceUpdateEvent;
import com.notification_service.kafka.dto.TransactionCompletedEvent;
import com.notification_service.kafka.dto.UserRegisteredEvent;
import com.notification_service.repository.ProcessedEventRepository;
import com.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerListener {

    private final EmailService emailService;
    private final ProcessedEventRepository processedEventRepository;

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroup;

    @KafkaListener(
            topics = "${app.kafka.topics.user-registered}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeUserRegisteredEvent(
            ConsumerRecord<String, UserRegisteredEvent> record
    ) {
        UserRegisteredEvent event = record.value();

        if (processedEventRepository.existsByEventIdAndConsumerGroup(
                event.getEventId(),
                consumerGroup
        )) {

            log.warn(
                    "Duplicate event detected. eventId={}, consumerGroup={}",
                    event.getEventId(),
                    consumerGroup
            );

            return;
        }

        log.info(
                "Received UserRegisteredEvent. topic={}, partition={}, offset={}, key={}, eventId={}, email={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                event.getEventId(),
                event.getEmail()
        );
        emailService.sendWelcomeEmail(event);

        processedEventRepository.save(
                ProcessedEvent.builder()
                        .eventId(event.getEventId())
                        .consumerGroup(consumerGroup)
                        .processedAt(LocalDateTime.now())
                        .build()
        );

        log.info(
                "UserRegisteredEvent processed successfully. eventId={}",
                event.getEventId()
        );


    }

    @KafkaListener(
            topics = "${app.kafka.topics.transaction-completed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTransactionCompletedEvent(
            ConsumerRecord<String, TransactionCompletedEvent> record
    ) {
        TransactionCompletedEvent event = record.value();

        log.info(
                "Received TransactionCompletedEvent. topic={}, partition={}, offset={}, key={}, eventId={}, reference={}",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key(),
                event.getEventId(),
                event.getTransactionReference()
        );

        if (processedEventRepository.existsByEventIdAndConsumerGroup(
                event.getEventId(),
                consumerGroup
        )) {

            log.warn(
                    "Duplicate TransactionCompletedEvent. eventId={}, reference={}",
                    event.getEventId(),
                    event.getTransactionReference()
            );

            return;
        }

        emailService.sendTransactionAlertEmail(
                toBalanceAlert(event, TransactionDirection.DEBIT)
        );

        emailService.sendTransactionAlertEmail(
                toBalanceAlert(event, TransactionDirection.CREDIT)
        );

        processedEventRepository.save(
                ProcessedEvent.builder()
                        .eventId(event.getEventId())
                        .consumerGroup(consumerGroup)
                        .processedAt(LocalDateTime.now())
                        .build()
        );

        log.info(
                "TransactionCompletedEvent processed successfully. eventId={}, reference={}",
                event.getEventId(),
                event.getTransactionReference()
        );
    }

    private BalanceUpdateEvent toBalanceAlert(
            TransactionCompletedEvent event,
            TransactionDirection direction
    ) {
        boolean debit = TransactionDirection.DEBIT.equals(direction);

        return BalanceUpdateEvent.builder()
                .accountNumber(
                        debit
                                ? event.getFromAccountNumber()
                                : event.getToAccountNumber()
                )
                .email(
                        debit
                                ? event.getFromEmail()
                                : event.getToEmail()
                )
                .firstName(
                        debit
                                ? event.getFromFirstName()
                                : event.getToFirstName()
                )
                .currentBalance(
                        debit
                                ? event.getFromCurrentBalance()
                                : event.getToCurrentBalance()
                )
                .amount(event.getAmount())
                .currency(Currency.valueOf(event.getCurrency()))
                .transactionDirection(direction)
                .transactionStatus(
                        TransactionStatus.valueOf(event.getStatus())
                )
                .reference(event.getTransactionReference())
                .description(event.getDescription())
                .correlationId(event.getCorrelationId())
                .build();
    }
}