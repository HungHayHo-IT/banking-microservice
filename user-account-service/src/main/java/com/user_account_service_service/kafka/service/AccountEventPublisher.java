package com.user_account_service_service.kafka.service;

import com.user_account_service_service.config.KafkaTopicProperties;
import com.user_account_service_service.kafka.dto.BalanceUpdateEvent;
import com.user_account_service_service.kafka.dto.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicProperties topics;

    public CompletableFuture<SendResult<String, Object>>
    publishUserRegisteredEvent(UserRegisteredEvent event) {

        String key = event.getUserId().toString();

        return publish(
                topics.getUserRegistered(),
                key,
                event
        );
    }

    public CompletableFuture<SendResult<String, Object>>
    publishBalanceUpdateNotification(BalanceUpdateEvent event) {

        String key = event.getAccountNumber();

        return publish(
                topics.getBalanceUpdateNotification(),
                key,
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