package com.user_account_service_service.kafka.dto;

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
@NoArgsConstructor
@AllArgsConstructor
public class TransferSagaEvent {

    private UUID eventId;

    private String eventType;

    private Integer eventVersion;

    private Instant occurredAt;

    private String correlationId;

    private String transactionReference;

    private String fromAccountNumber;

    private String toAccountNumber;

    private BigDecimal amount;

    private String currency;

    private String reason;
}