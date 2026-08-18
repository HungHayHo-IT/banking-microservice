package com.transaction_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalTransferRequest {

    private String reference;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
}