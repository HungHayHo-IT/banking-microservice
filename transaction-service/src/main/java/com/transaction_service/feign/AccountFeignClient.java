package com.transaction_service.feign;

import com.transaction_service.dto.AccountDTO;
import com.transaction_service.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-account-service")
public interface AccountFeignClient {

    @GetMapping("/api/accounts/{accountNumber}")
    ApiResponse<AccountDTO> getAccountByNumber(@PathVariable("accountNumber") String accountNumber);
}
