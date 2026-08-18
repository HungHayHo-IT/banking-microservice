package com.transaction_service.feign;

import com.transaction_service.dto.AccountDTO;
import com.transaction_service.dto.ApiResponse;
import com.transaction_service.dto.InternalTransferRequest;
import com.transaction_service.dto.InternalTransferResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "user-account-service",
        configuration = FeignConfig.class
)
public interface AccountFeignClient {

    @GetMapping("/api/accounts/{accountNumber}")
    ApiResponse<AccountDTO> getAccountByNumber(
            @PathVariable("accountNumber") String accountNumber,
            @RequestHeader("banknow-correlation-id") String correlationId
    );

    @PostMapping("/internal/accounts/transfer")
    ApiResponse<InternalTransferResponse> transfer(
            @RequestBody InternalTransferRequest request,

            @RequestHeader("banknow-correlation-id")
            String correlationId
    );
}