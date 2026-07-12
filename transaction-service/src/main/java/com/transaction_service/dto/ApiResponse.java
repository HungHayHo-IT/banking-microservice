package com.transaction_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Cấu trúc response chuẩn cho toàn bộ API")
public record ApiResponse<T>(
        @Schema(description = "HTTP status code", example = "200")
        int statusCode,

        @Schema(description = "Thông điệp mô tả kết quả xử lý", example = "Success")
        String message,

        @Schema(description = "Dữ liệu trả về, tùy theo từng API")
        T data
) {
}