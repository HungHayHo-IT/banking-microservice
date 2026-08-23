package com.transaction_service.kafka.service;

import com.transaction_service.config.KafkaTopicProperties;
import com.transaction_service.kafka.dto.BalanceUpdateEvent;
import com.transaction_service.kafka.dto.TransactionCompletedEvent;
import com.transaction_service.kafka.dto.TransactionFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public CompletableFuture<SendResult<String, Object>>
    publishBalanceUpdateRequested(BalanceUpdateEvent event) {

        return publish(
                topics.getBalanceUpdateRequested(),
                event.getAccountNumber(),
                event
        );
    }

    public CompletableFuture<SendResult<String, Object>>
    publishBalanceUpdateNotification(BalanceUpdateEvent event) {

        return publish(
                topics.getBalanceUpdateNotification(),
                event.getReference(),
                event
        );
    }

    public CompletableFuture<SendResult<String, Object>>
    publishTransactionCompleted(TransactionCompletedEvent event) {

        return publish(
                topics.getTransactionCompleted(),
                event.getTransactionReference(),
                event
        );
    }

    public CompletableFuture<SendResult<String, Object>>
    publishTransactionFailed(TransactionFailedEvent event) {

        return publish(
                topics.getTransactionFailed(),
                event.getTransactionReference(),
                event
        );
    }

    private CompletableFuture<SendResult<String, Object>> publish(
            String topic,
            String key,
            Object event) {

        try {
            return kafkaTemplate
                    .send(topic, key, event)
                    .whenComplete((result, exception) -> {

                        if (exception != null) {
                            log.error(
                                    "Kafka publish failed. topic={}, key={}, eventType={}",
                                    topic,
                                    key,
                                    event.getClass().getSimpleName(),
                                    exception
                            );
                            return;
                        }

                        var metadata = result.getRecordMetadata();

                        log.info(
                                "Kafka publish succeeded. topic={}, partition={}, offset={}, key={}, eventType={}",
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset(),
                                key,
                                event.getClass().getSimpleName()
                        );
                    });

        } catch (RuntimeException exception) {
            log.error(
                    "Kafka send could not be started. topic={}, key={}, eventType={}",
                    topic,
                    key,
                    event.getClass().getSimpleName(),
                    exception
            );

            return CompletableFuture.failedFuture(exception);
        }
    }
}