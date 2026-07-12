package com.user_account_service_service.controller;

import com.user_account_service_service.dto.AccountDTO;
import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.enums.AccountStatus;
import com.user_account_service_service.service.AccountsService;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@Tag(name = "Admin - Account", description = "APIs quản trị tài khoản, chỉ dành cho người dùng có quyền ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class AdminAccountController {

    private final AccountsService accountService;

    @GetMapping("/all")
    @Operation(
            summary = "Lấy danh sách tất cả tài khoản (phân trang)",
            description = "Trả về danh sách toàn bộ tài khoản trong hệ thống, hỗ trợ phân trang. " +
                    "Yêu cầu quyền ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách tài khoản thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Người dùng không có quyền ADMIN"
            )
    })
    public ResponseEntity<ApiResponse<Page<AccountDTO>>> listAllAccounts(
            @ParameterObject
            @PageableDefault(page = 0, size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(accountService.getAllAccounts(pageable));
    }

    @PatchMapping("/status")
    @Operation(
            summary = "Thay đổi trạng thái tài khoản",
            description = "Cập nhật trạng thái (ACTIVE, FROZEN, CLOSED) của một tài khoản theo số tài khoản. " +
                    "Yêu cầu quyền ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật trạng thái tài khoản thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "accountNumber hoặc status không hợp lệ"
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
                    description = "Không tìm thấy tài khoản với số tài khoản đã cho"
            )
    })
    public ResponseEntity<ApiResponse<AccountDTO>> changeAccountStatus(
            @Parameter(description = "Số tài khoản cần thay đổi trạng thái", required = true, example = "1234567890")
            @RequestParam String accountNumber,

            @Parameter(description = "Trạng thái mới của tài khoản", required = true, example = "FROZEN",
                    schema = @Schema(implementation = AccountStatus.class))
            @RequestParam AccountStatus status
    ) {
        return ResponseEntity.ok(accountService.changeAccountStatus(accountNumber, status));
    }
}