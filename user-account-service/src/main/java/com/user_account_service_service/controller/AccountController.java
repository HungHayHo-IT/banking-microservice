package com.user_account_service_service.controller;

import com.user_account_service_service.dto.AccountDTO;
import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.service.AccountsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountsService accountsService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountDTO>> getMyAccount() {
        return ResponseEntity.ok(accountsService.getMyAccount());
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountByNumber(
            @RequestHeader("banknow-correlation-id") String correlationId,
            @PathVariable String accountNumber
    ) {
        logger.debug("banknow-correlation-id: " , correlationId);
        return ResponseEntity.ok(accountsService.getAccountNumber(accountNumber,correlationId));
    }
}
