package com.transaction_service.kafka.dto.saga;

import com.transaction_service.enums.Currency;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestedEvent {

    private String eventId;

    private String reference;

    private String accountNumber;

    private BigDecimal amount;

    private Currency currency;

    private String correlationId;
}