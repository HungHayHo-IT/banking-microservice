package com.user_account_service_service.service;

import com.user_account_service_service.dto.AccountDTO;
import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.enums.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountsService {
    ApiResponse<AccountDTO> getMyAccount();

    ApiResponse<AccountDTO> getAccountNumber(String accountNumber);

    ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus status);

    ApiResponse<Page<AccountDTO>> getAllAccounts(Pageable pageable);

}
