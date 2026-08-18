package com.transaction_service.service;

import com.transaction_service.dto.*;
import com.transaction_service.enums.TransactionDirection;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    ApiResponse<TransactionDTO> deposit(DepositRequest request, String correlationid);

    ApiResponse<TransactionDTO> transfer(TransferRequest request, String correlationid);

    ApiResponse<TransactionDTO> withdraw(WithdrawRequest request,String correlationid);


    ApiResponse<TransactionDTO> getTransactionByReference(String reference);

    ApiResponse<List<TransactionDTO>> getAllTransactionHistoryOfAnAccountNumber(String accountNumber);

    ApiResponse<List<TransactionDTO>> getTransactionHistory(String accountNumber, LocalDateTime start, LocalDateTime end);

    ApiResponse<List<TransactionDTO>> getMyTransactionHistoryByDirection(String accountNumber, TransactionDirection direction);
}
