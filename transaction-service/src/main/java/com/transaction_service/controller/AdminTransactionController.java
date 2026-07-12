package com.transaction_service.controller;

import com.transaction_service.dto.ApiResponse;
import com.transaction_service.dto.TransactionDTO;
import com.transaction_service.dto.TransactionRequest;
import com.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions/admin")
@PreAuthorize("hasAuthority('ADMIN')")
@Tag(name = "Admin - Transaction", description = "APIs quản trị giao dịch, chỉ dành cho người dùng có quyền ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class AdminTransactionController {

    private final TransactionService transactionService;
    private final Logger logger = LoggerFactory.getLogger(AdminTransactionController.class);

    @PostMapping("/deposit")
    @Operation(
            summary = "Nạp tiền vào tài khoản (deposit)",
            description = "Thực hiện nạp tiền trực tiếp vào một tài khoản (toAccountNumber), không cần tài khoản nguồn (fromAccountNumber). " +
                    "Sau khi xử lý thành công, hệ thống sẽ publish sự kiện Kafka (balance-update-events) để cập nhật số dư. " +
                    "Yêu cầu quyền ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Nạp tiền thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ (thiếu toAccountNumber, amount <= 0...)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Người dùng không có quyền ADMIN"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy tài khoản đích (toAccountNumber)"
            )
    })
    public ResponseEntity<ApiResponse<TransactionDTO>> deposit(
            @Valid @RequestBody TransactionRequest request,

            @Parameter(description = "Correlation ID dùng để tracing request giữa các service", required = true)
            @RequestHeader("banknow-correlation-id") String correlationId
    ) {
        logger.debug("bankapp-correlation-id found: {}", correlationId);
        return ResponseEntity.ok(transactionService.deposit(request, correlationId));
    }
}