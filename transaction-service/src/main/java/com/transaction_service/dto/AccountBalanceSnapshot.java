package com.transaction_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceSnapshot {

    private String accountNumber;
    private String email;
    private String firstName;
    private BigDecimal currentBalance;
}