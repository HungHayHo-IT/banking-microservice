package com.user_account_service_service.kafka.dto.saga;

import com.user_account_service_service.enums.Currency;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitRequestedEvent {

    private String eventId;

    private String reference;

    private String fromAccountNumber;

    private String toAccountNumber;

    private BigDecimal amount;

    private Currency currency;

    private String correlationId;
}