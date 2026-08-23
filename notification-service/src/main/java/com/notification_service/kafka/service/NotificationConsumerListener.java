package com.notification_service.kafka.service;

import com.notification_service.kafka.dto.BalanceUpdateEvent;
import com.notification_service.kafka.dto.UserRegisteredEvent;
import com.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumerListener {

    private final EmailService emailService;

    @KafkaListener(
            topics = "${app.kafka.topics.user-registered}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeUserRegisteredEvent(UserRegisteredEvent event) {

        log.info(
                "Received UserRegisteredEvent. eventId={}, userId={}, email={}",
                event.getEventId(),
                event.getUserId(),
                event.getEmail()
        );

        try {
            emailService.sendWelcomeEmail(event);
        } catch (Exception exception) {
            log.error(
                    "Welcome email failed. eventId={}, userId={}",
                    event.getEventId(),
                    event.getUserId(),
                    exception
            );
        }
    }

    @KafkaListener(topics = "balance-update-notification-events", groupId = "notification-group")
    public void consumerBalanceUpdateEvent(BalanceUpdateEvent event) {
        log.info(
                "Received transaction notification. reference={}, correlationId={}",
                event.getReference(),
                event.getCorrelationId()
        );
        try {
            emailService.sendTransactionAlertEmail(event);

        } catch (Exception e) {

            log.error("Error sending email our: {}", e.getMessage());
        }
    }

}
