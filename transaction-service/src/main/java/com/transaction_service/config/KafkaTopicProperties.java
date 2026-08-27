package com.transaction_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicProperties {

    private String balanceUpdateRequested;
    private String balanceUpdateNotification;
    private String transactionCompleted;
    private String transactionFailed;

    public static final String TRANSACTION_DEBIT_REQUESTED =
            "banking.transaction.debit.requested";

    public static final String TRANSACTION_DEBIT_COMPLETED =
            "banking.transaction.debit.completed";

    public static final String TRANSACTION_DEBIT_FAILED =
            "banking.transaction.debit.failed";

    public static final String TRANSACTION_CREDIT_REQUESTED =
            "banking.transaction.credit.requested";

    public static final String TRANSACTION_CREDIT_COMPLETED =
            "banking.transaction.credit.completed";

    public static final String TRANSACTION_CREDIT_FAILED =
            "banking.transaction.credit.failed";

    public static final String TRANSACTION_REFUND_REQUESTED =
            "banking.transaction.refund.requested";
}