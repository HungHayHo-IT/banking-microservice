package com.transaction_service.feign;

import com.transaction_service.dto.AccountDTO;
import com.transaction_service.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-account-service")
public interface AccountFeignClient {

    @GetMapping(value = "/api/accounts/{accountNumber}",consumes = "application/json")
    ApiResponse<AccountDTO> getAccountByNumber(@RequestHeader("banknow-correlation-id") String correlationId, @PathVariable("accountNumber") String accountNumber);
}
