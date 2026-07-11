package com.transaction_service.controller;

import com.transaction_service.dto.ApiResponse;
import com.transaction_service.dto.TransactionDTO;
import com.transaction_service.dto.TransactionRequest;
import com.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminTransactionController {
    private final TransactionService transactionService;
    private final Logger logger = LoggerFactory.getLogger(AdminTransactionController.class);

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionDTO>> deposit(@Valid @RequestBody TransactionRequest request , @RequestHeader("banknow-correlation-id") String correlationId) {
        logger.debug("bankapp-correlation-id found: {}" , correlationId);
        return ResponseEntity.ok(transactionService.deposit(request,correlationId));
    }
}
