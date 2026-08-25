package com.notification_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisteredEvent {

    private UUID eventId;
    private String eventType;
    private int eventVersion;
    private Instant occurredAt;
    private String correlationId;
    private String aggregateId;

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String bankName;
}