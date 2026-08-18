package com.transaction_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InternalTransferResponse {

    private AccountBalanceSnapshot debitAccount;
    private AccountBalanceSnapshot creditAccount;
}