package com.user_account_service_service.controller;

import com.user_account_service_service.dto.AccountDTO;
import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.service.AccountsService;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Account", description = "APIs quản lý thông tin và truy vấn tài khoản người dùng")
public class AccountController {

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountsService accountsService;

    @GetMapping("/me")
    @Operation(
            summary = "Lấy thông tin tài khoản của người dùng hiện tại",
            description = "Trả về thông tin tài khoản (account) gắn với người dùng đang đăng nhập, " +
                    "được xác định thông qua thông tin xác thực (JWT) trong request."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thông tin tài khoản thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy tài khoản tương ứng với người dùng"
            )
    })
    public ResponseEntity<ApiResponse<AccountDTO>> getMyAccount() {
        return ResponseEntity.ok(accountsService.getMyAccount());
    }

    @GetMapping("/{accountNumber}")
    @Operation(
            summary = "Lấy thông tin tài khoản theo số tài khoản",
            description = "Truy vấn thông tin chi tiết của một tài khoản dựa trên số tài khoản (accountNumber). " +
                    "Yêu cầu header 'banknow-correlation-id' để phục vụ tracing giữa các microservice."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thông tin tài khoản thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Thiếu header 'banknow-correlation-id' hoặc accountNumber không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy tài khoản với số tài khoản đã cho"
            )
    })
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountByNumber(
            @Parameter(description = "Correlation ID dùng để tracing request giữa các service", required = true)
            @RequestHeader("banknow-correlation-id") String correlationId,
            @Parameter(description = "Số tài khoản cần truy vấn", required = true, example = "1234567890")
            @PathVariable String accountNumber
    ) {
        logger.debug("banknow-correlation-id: {}", correlationId);
        return ResponseEntity.ok(accountsService.getAccountNumber(accountNumber, correlationId));
    }
}