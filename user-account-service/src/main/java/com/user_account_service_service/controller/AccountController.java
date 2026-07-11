package com.user_account_service_service.controller;

import com.user_account_service_service.dto.AccountDTO;
import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.service.AccountsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountsService accountsService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountDTO>> getMyAccount() {
        return ResponseEntity.ok(accountsService.getMyAccount());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountByNumber(
            @PathVariable String accountNumber
    ) {
        return ResponseEntity.ok(accountsService.getAccountNumber(accountNumber));
    }
}
