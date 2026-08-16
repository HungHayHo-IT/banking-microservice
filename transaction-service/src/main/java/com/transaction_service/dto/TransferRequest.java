package com.transaction_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {

    @NotBlank(message = "Source account number is required")
    @Pattern(
            regexp = "\\d{10}",
            message = "Source account number must contain exactly 10 digits"
    )
    private String fromAccountNumber;

    @NotBlank(message = "Destination account number is required")
    @Pattern(
            regexp = "\\d{10}",
            message = "Destination account number must contain exactly 10 digits"
    )
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than zero"
    )
    @Digits(
            integer = 15,
            fraction = 2,
            message = "Amount format is invalid"
    )
    private BigDecimal amount;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}