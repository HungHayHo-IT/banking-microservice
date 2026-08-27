package com.transaction_service.kafka.dto.saga;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditFailedEvent {

    private String eventId;

    private String reference;

    private String accountNumber;

    private String reason;

    private String correlationId;
}