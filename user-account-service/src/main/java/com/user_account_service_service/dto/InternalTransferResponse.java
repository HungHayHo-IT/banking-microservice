package com.user_account_service_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalTransferResponse {

    private AccountBalanceSnapshot debitAccount;
    private AccountBalanceSnapshot creditAccount;
}