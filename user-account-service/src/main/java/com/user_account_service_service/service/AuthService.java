package com.user_account_service_service.service;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.AuthResponse;
import com.user_account_service_service.dto.LoginRequest;
import com.user_account_service_service.dto.RegistrationRequest;

public interface AuthService {

    ApiResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest);

    ApiResponse<AuthResponse> loginUser(LoginRequest loginRequest);

}
