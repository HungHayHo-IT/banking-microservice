package com.notification_service.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionCompletedEvent {

    private UUID eventId;
    private Instant occurredAt;
    private String eventType;
    private int eventVersion;
    private String aggregateId;

    private String transactionReference;
    private String transactionType;

    private String fromAccountNumber;
    private String fromEmail;
    private String fromFirstName;
    private BigDecimal fromCurrentBalance;

    private String toAccountNumber;
    private String toEmail;
    private String toFirstName;
    private BigDecimal toCurrentBalance;

    private BigDecimal amount;
    private String currency;
    private String status;
    private String description;
    private String correlationId;
}