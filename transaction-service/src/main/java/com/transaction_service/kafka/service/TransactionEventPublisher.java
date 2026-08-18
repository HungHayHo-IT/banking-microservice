package com.transaction_service.kafka.service;

import com.transaction_service.kafka.dto.BalanceUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventPublisher {


    private final KafkaTemplate<String,Object> kafkaTemplate;
    private static final String NOTIFICATION_TOPIC =
            "balance-update-notification-events";

    public void sendBalanceUpdate(BalanceUpdateEvent event) {
        kafkaTemplate.send("balance-update-events", event.getAccountNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {

                        String topicResult = result.getRecordMetadata().topic();
                        long topicOffset = result.getRecordMetadata().offset();

                        log.info("SENT MESSAGE to TOPIC {} OFFSET {}", topicResult, topicOffset);

                    }
                });
    }

    public void sendTransactionNotification(
            BalanceUpdateEvent event) {

        kafkaTemplate.send(
                NOTIFICATION_TOPIC,
                event.getReference(),
                event
        ).whenComplete((result, exception) -> {

            if (exception == null) {
                log.info(
                        "Notification event sent. reference={}, correlationId={}",
                        event.getReference(),
                        event.getCorrelationId()
                );
            } else {
                /*
                 * Chỉ log lỗi.
                 * Không đổi transaction từ SUCCESS sang FAILED
                 * chỉ vì gửi email thất bại.
                 */
                log.error(
                        "Notification event failed. reference={}, correlationId={}",
                        event.getReference(),
                        event.getCorrelationId(),
                        exception
                );
            }
        });
    }
}
