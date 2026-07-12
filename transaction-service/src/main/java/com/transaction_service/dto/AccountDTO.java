package com.transaction_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.transaction_service.enums.AccountStatus;
import com.transaction_service.enums.AccountType;
import com.transaction_service.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin chi tiết một tài khoản ngân hàng")
public class AccountDTO {

    @Schema(description = "ID nội bộ của tài khoản", example = "1")
    private Long id;

    @Schema(description = "Số tài khoản", example = "1234567890")
    private String accountNumber;

    @Schema(description = "Số dư hiện tại của tài khoản", example = "1500000.00")
    private BigDecimal balance;

    @Schema(description = "Loại tiền tệ của tài khoản", example = "VND")
    private Currency currency; // USD, EURO, VND

    @Schema(description = "Loại tài khoản", example = "SAVINGS")
    private AccountType accountType; // SAVINGS, CURRENT

    @Schema(description = "Trạng thái tài khoản", example = "ACTIVE")
    private AccountStatus accountStatus; // ACTIVE, CLOSED, FROZEN

    @Schema(description = "Email chủ sở hữu tài khoản", example = "hung.nguyen@gmail.com")
    private String ownerEmail;

    @Schema(description = "Thời điểm tài khoản được tạo", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
}