package com.transaction_service.kafka.dto;

import com.transaction_service.enums.Currency;
import com.transaction_service.enums.TransactionDirection;
import com.transaction_service.enums.TransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BalanceChangedEvent {

    // Event metadata
    private UUID eventId;
    private String eventType;
    private int eventVersion;
    private Instant occurredAt;
    private String correlationId;
    private String aggregateId;

    // Business data
    private String accountNumber;
    private BigDecimal amount;
    private TransactionDirection transactionDirection;
    private TransactionStatus transactionStatus;
    private String reference;
    private Currency currency;
    private String description;
}