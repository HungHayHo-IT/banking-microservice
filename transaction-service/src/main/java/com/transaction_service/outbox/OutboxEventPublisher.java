package com.transaction_service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction_service.entity.OutboxEvent;
import com.transaction_service.enums.OutboxStatus;
import com.transaction_service.kafka.dto.BalanceUpdateEvent;
import com.transaction_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop100ByStatusOrderByCreatedAtAsc(
                                OutboxStatus.PENDING
                        );

        for (OutboxEvent event : events) {

            try {

                BalanceUpdateEvent balanceUpdateEvent =
                        objectMapper.readValue(
                                event.getPayload(),
                                BalanceUpdateEvent.class
                        );

                // Gửi event và CHỜ Kafka xác nhận
                kafkaTemplate.send(
                        "balance-update-events",
                        event.getAggregateId(),
                        balanceUpdateEvent
                ).get(5, TimeUnit.SECONDS);

                // Chỉ đánh dấu SENT sau khi Kafka ACK thành công
                event.setStatus(OutboxStatus.SENT);
                event.setPublishedAt(LocalDateTime.now());

                outboxEventRepository.save(event);

                log.info(
                        "Outbox event published successfully. eventId={}",
                        event.getId()
                );

            } catch (Exception e) {

                event.setRetryCount(
                        event.getRetryCount() + 1
                );

                outboxEventRepository.save(event);

                log.error(
                        "Failed to publish outbox event. eventId={}, retryCount={}",
                        event.getId(),
                        event.getRetryCount(),
                        e
                );
            }
        }
    }
}