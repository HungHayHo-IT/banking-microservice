package com.user_account_service_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.user_account_service_service.enums.AccountStatus;
import com.user_account_service_service.enums.AccountType;
import com.user_account_service_service.enums.Currency;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private Currency currency; // USD, EURO ,VND

    private AccountType accountType; //SAVINGS, CURRENT

    private AccountStatus accountStatus; //ACTIVE, CLOSED, FROZEN

    private String ownerEmail;

    private LocalDateTime createdAt;
}
