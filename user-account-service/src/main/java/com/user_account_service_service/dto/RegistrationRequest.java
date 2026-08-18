package com.user_account_service_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72,
            message = "Password must contain between 8 and 72 characters")
    private String password;

    @NotBlank(message = "Firstname is required")
    @Size(max = 100, message = "Firstname must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Lastname must not exceed 100 characters")
    private String lastName;
}