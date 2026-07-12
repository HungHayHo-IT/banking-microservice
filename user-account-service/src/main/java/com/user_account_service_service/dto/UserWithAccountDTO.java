package com.user_account_service_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Thông tin người dùng kèm tài khoản ngân hàng liên kết")
public class UserWithAccountDTO {

    @Schema(description = "Thông tin người dùng")
    private UserDTO user;

    @Schema(description = "Thông tin tài khoản ngân hàng của người dùng")
    private AccountDTO account;
}