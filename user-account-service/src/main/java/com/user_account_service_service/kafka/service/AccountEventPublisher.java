package com.user_account_service_service.kafka.service;

import com.user_account_service_service.kafka.dto.BalanceUpdateEvent;
import com.user_account_service_service.kafka.dto.UserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    private static final  String USER_TOPIC = "user-registered-events";
    private static final String BALANCE_UPDATE_TOPIC = "balance-update-notification-events";

    public void publishedUserRegistrationEvent(UserRegistrationEvent event){
        try{
            kafkaTemplate.send(USER_TOPIC,event.getEmail(),event);
            log.info("Event Sent Out {}", event.getEmail());

        }catch (Exception e){
            log.info("Failed to publish user registered event: {}", e.getMessage());
        }
    }

    public void publishTransactionNotificationEvent(BalanceUpdateEvent event){
        try {
            kafkaTemplate.send(BALANCE_UPDATE_TOPIC, event.getEmail(), event);
            log.info("Balance Update Event Sent Out {}", event.getEmail());

        } catch (Exception e) {
            log.info("Failed to Balance Update event: {}", e.getMessage());
        }
    }


}
