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
}