package com.user_account_service_service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsDTO {

    private long totalUsers;
    private long activeUsers;
    private long inactiveUsers;
    private long totalAccounts;
    private long averageAccountPerUser;
    private long customersCount;
    private long adminsCount;
}
