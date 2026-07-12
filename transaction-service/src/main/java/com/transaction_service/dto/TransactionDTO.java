package com.transaction_service.dto;

import com.transaction_service.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Thông tin chi tiết một giao dịch")
public class TransactionDTO {

    @Schema(description = "ID nội bộ của giao dịch", example = "1001")
    private Long id;

    @Schema(description = "Mã tham chiếu duy nhất của giao dịch", example = "TXN-20260712-0001")
    private String reference;

    @Schema(description = "Số tài khoản nguồn (rỗng nếu là deposit)", example = "1234567890")
    private String fromAccountNumber;

    @Schema(description = "Mã ngân hàng của tài khoản nguồn", example = "BANKNOW")
    private String fromBankCode;

    @Schema(description = "Số tài khoản đích", example = "0987654321")
    private String toAccountNumber;

    @Schema(description = "Mã ngân hàng của tài khoản đích", example = "BANKNOW")
    private String toBankCode;

    @Schema(description = "Số tiền giao dịch", example = "500000.00")
    private BigDecimal amount;

    @Schema(description = "Mô tả/ghi chú giao dịch", example = "Nạp tiền lương tháng 7")
    private String description;

    @Schema(description = "Loại tiền tệ", example = "VND")
    private Currency currency;

    @Schema(description = "Loại giao dịch", example = "DEPOSIT")
    private TransactionType transactionType;

    @Schema(description = "Trạng thái giao dịch", example = "SUCCESS")
    private TransactionStatus transactionStatus;

    @Schema(description = "Chiều giao dịch (ghi có/ghi nợ)", example = "CREDIT")
    private TransactionDirection transactionDirection;

    @Schema(description = "Kênh thực hiện giao dịch", example = "MOBILE")
    private Channel channel;

    @Schema(description = "Thời điểm giao dịch được tạo", example = "2026-07-12T10:30:00")
    private LocalDateTime createdAt;
}