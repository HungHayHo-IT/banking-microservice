package com.user_account_service_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {

    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Firstname is required")
    private String firstName;

    private String lastName;
    private String role;
}
