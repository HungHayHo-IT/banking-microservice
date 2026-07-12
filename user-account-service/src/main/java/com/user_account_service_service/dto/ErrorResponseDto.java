package com.user_account_service_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Cấu trúc response trả về khi có lỗi xảy ra")
public class ErrorResponseDto {

    @Schema(description = "Đường dẫn API xảy ra lỗi", example = "/api/accounts/1234567890")
    private String apiPath;

    @Schema(description = "Mã lỗi HTTP", example = "NOT_FOUND")
    private HttpStatus errorCode;

    @Schema(description = "Thông điệp mô tả lỗi", example = "Account Number not found")
    private String errorMessage;

    @Schema(description = "Thời điểm xảy ra lỗi", example = "2026-07-12T10:30:00")
    private LocalDateTime localDateTime;
}