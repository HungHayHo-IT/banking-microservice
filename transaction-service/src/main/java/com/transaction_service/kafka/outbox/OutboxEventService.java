package com.transaction_service.kafka.outbox;

public interface OutboxEventService {

    void saveEvent(
            String aggregateId,
            String eventType,
            Object payload
    );
}