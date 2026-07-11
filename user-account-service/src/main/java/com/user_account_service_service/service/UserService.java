package com.user_account_service_service.service;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.UserDTO;
import com.user_account_service_service.dto.UserStatisticsDTO;
import com.user_account_service_service.dto.UserWithAccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    ApiResponse<UserWithAccountDTO> getMyDetails();

    ApiResponse<UserWithAccountDTO> searchUser(String email, String accountNumber);

    ApiResponse<Page<UserDTO>> getAllUser(String roleName, Pageable pageable);

    ApiResponse<UserStatisticsDTO> getUserStatistics();

    //chuyen doi trang thai hoat dong
    ApiResponse<String> toggleUserStatus(Long userId);
}
