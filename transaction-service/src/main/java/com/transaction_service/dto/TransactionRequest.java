package com.transaction_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thông tin yêu cầu giao dịch (nạp tiền/rút tiền/chuyển khoản)")
public class TransactionRequest {

    @Schema(description = "Số tài khoản nguồn. Bỏ trống nếu là giao dịch nạp tiền (deposit)", example = "1234567890")
    private String fromAccountNumber;

    @NotBlank(message = "Destination account number is required")
    @Schema(description = "Số tài khoản đích nhận tiền", example = "0987654321", requiredMode = Schema.RequiredMode.REQUIRED)
    private String toAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Schema(description = "Số tiền giao dịch, phải lớn hơn 0", example = "500000.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Mô tả/ghi chú giao dịch", example = "Nạp tiền lương tháng 7")
    private String description;
}