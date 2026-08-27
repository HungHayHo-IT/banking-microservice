package com.transaction_service.kafka.outbox.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction_service.entity.OutboxEvent;
import com.transaction_service.enums.OutboxStatus;
import com.transaction_service.kafka.outbox.OutboxEventService;
import com.transaction_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveEvent(
            String aggregateId,
            String eventType,
            Object payload
    ) {

        try {

            String jsonPayload =
                    objectMapper.writeValueAsString(payload);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(jsonPayload)
                    .status(OutboxStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .publishedAt(null)
                    .retryCount(0)
                    .build();

            outboxEventRepository.save(event);

        } catch (JsonProcessingException exception) {

            throw new IllegalStateException(
                    "Could not serialize outbox event",
                    exception
            );
        }
    }
}