package com.user_account_service_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Kết quả trả về sau khi đăng ký/đăng nhập thành công")
public class AuthResponse {

    @Schema(description = "JWT access token dùng cho các request tiếp theo", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJodW5nQGdtYWlsLmNvbSJ9.abc123")
    private String token;

    @Schema(description = "Thông tin người dùng vừa đăng ký/đăng nhập")
    private UserDTO user;
}