package com.user_account_service_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Thông tin người dùng")
public class UserDTO {

    @Schema(description = "ID nội bộ của người dùng", example = "10")
    private Long id;

    @Schema(description = "Email người dùng", example = "hung.nguyen@gmail.com")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Mật khẩu (chỉ dùng khi ghi, không trả về trong response)", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @Schema(description = "Tên", example = "Hung")
    private String firstName;

    @Schema(description = "Họ", example = "Nguyen")
    private String lastName;

    @Schema(description = "Trạng thái tài khoản có đang hoạt động hay không", example = "true")
    private boolean enabled;

    @Schema(description = "Danh sách role được gán cho người dùng")
    private Set<RoleDTO> roles;

    @Schema(description = "Thời điểm người dùng được tạo", example = "2026-01-15T10:30:00")
    private LocalDateTime createdAt;
}