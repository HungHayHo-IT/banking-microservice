package com.transaction_service.saga;

public enum SagaStatus {

    STARTED,

    DEBIT_PENDING,

    DEBIT_COMPLETED,

    CREDIT_PENDING,

    COMPLETED,

    COMPENSATING,

    COMPENSATED,

    FAILED
}