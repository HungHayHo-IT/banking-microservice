package com.notification_service.repository;

import com.notification_service.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByEventIdAndConsumerGroup(
            UUID eventId,
            String consumerGroup
    );
}