package com.user_account_service_service.controller;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.UserWithAccountDTO;
import com.user_account_service_service.service.UserService;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "APIs quản lý thông tin cá nhân của người dùng đang đăng nhập")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Lấy thông tin cá nhân của người dùng hiện tại",
            description = "Trả về thông tin người dùng kèm thông tin tài khoản ngân hàng liên kết, " +
                    "được xác định thông qua JWT token trong request."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thông tin cá nhân thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy thông tin người dùng"
            )
    })
    public ResponseEntity<ApiResponse<UserWithAccountDTO>> getMyProfile() {
        return ResponseEntity.ok(userService.getMyDetails());
    }
}