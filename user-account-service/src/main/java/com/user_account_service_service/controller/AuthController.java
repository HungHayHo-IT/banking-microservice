package com.user_account_service_service.controller;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.AuthResponse;
import com.user_account_service_service.dto.LoginRequest;
import com.user_account_service_service.dto.RegistrationRequest;
import com.user_account_service_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "APIs đăng ký và đăng nhập, không yêu cầu xác thực")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Đăng ký tài khoản mới",
            description = "Tạo một người dùng mới trong hệ thống. Nếu không truyền 'role', hệ thống sẽ gán role mặc định. " +
                    "Sau khi đăng ký, hệ thống sẽ publish sự kiện Kafka (user-registered-events) để notification-service gửi email chào mừng."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đăng ký thành công, trả về JWT token và thông tin người dùng"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ (thiếu email/password/firstName)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Email đã tồn tại trong hệ thống"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @RequestBody @Valid RegistrationRequest registrationRequest
    ) {
        return ResponseEntity.ok(authService.registerUser(registrationRequest));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Đăng nhập",
            description = "Xác thực người dùng bằng email và password, trả về JWT token nếu thành công."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Đăng nhập thành công, trả về JWT token và thông tin người dùng"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ (thiếu email/password)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Email hoặc mật khẩu không đúng"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.loginUser(loginRequest));
    }
}