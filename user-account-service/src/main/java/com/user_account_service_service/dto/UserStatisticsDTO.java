package com.user_account_service_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Thống kê tổng quan về người dùng trong hệ thống")
public class UserStatisticsDTO {

    @Schema(description = "Tổng số người dùng trong hệ thống", example = "150")
    private long totalUsers;

    @Schema(description = "Số người dùng đang active", example = "120")
    private long activeUsers;

    @Schema(description = "Số người dùng đang inactive", example = "30")
    private long inactiveUsers;

    @Schema(description = "Tổng số tài khoản ngân hàng trong hệ thống", example = "180")
    private long totalAccounts;

    @Schema(description = "Số tài khoản trung bình trên mỗi người dùng", example = "1")
    private long averageAccountPerUser;

    @Schema(description = "Số lượng người dùng có role CUSTOMER", example = "145")
    private long customersCount;

    @Schema(description = "Số lượng người dùng có role ADMIN", example = "5")
    private long adminsCount;
}