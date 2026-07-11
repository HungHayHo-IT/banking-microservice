package com.user_account_service_service.controller;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.AuthResponse;
import com.user_account_service_service.dto.LoginRequest;
import com.user_account_service_service.dto.RegistrationRequest;
import com.user_account_service_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody @Valid RegistrationRequest registrationRequest){
        return ResponseEntity.ok(authService.registerUser(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(authService.loginUser(loginRequest));
    }



}
