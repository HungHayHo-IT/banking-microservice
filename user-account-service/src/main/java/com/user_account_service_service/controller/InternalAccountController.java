package com.user_account_service_service.controller;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.InternalTransferRequest;
import com.user_account_service_service.dto.InternalTransferResponse;
import com.user_account_service_service.service.AccountsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/accounts")
@RequiredArgsConstructor
public class InternalAccountController {

    private final AccountsService accountsService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<InternalTransferResponse>> transfer(
            @Valid @RequestBody InternalTransferRequest request,

            @RequestHeader("banknow-correlation-id")
            String correlationId) {

        return ResponseEntity.ok(
                accountsService.transfer(request, correlationId)
        );
    }
}
