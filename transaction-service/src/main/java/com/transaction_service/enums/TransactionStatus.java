package com.transaction_service.enums;

public enum TransactionStatus {

    PENDING,

    // Saga - debit step
    DEBIT_RESERVED,
    DEBIT_FAILED,

    // Saga - credit step
    CREDIT_COMPLETED,
    CREDIT_FAILED,

    // Saga - compensation step
    REFUND_REQUESTED,
    COMPENSATED,

    // Final states
    COMPLETED,
    FAILED
}