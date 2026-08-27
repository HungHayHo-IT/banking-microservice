package com.user_account_service_service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicProperties {

    private String userRegistered;
    private String balanceUpdateNotification;

    private String transferDebitRequested;
    private String transferDebitReserved;
    private String transferDebitFailed;
    private String transferCreditRequested;
    private String transferCreditCompleted;
    private String transferCreditFailed;
    private String transferRefundRequested;
    private String transferCompensated;
}
