package com.notification_service.service;

import com.notification_service.kafka.dto.BalanceUpdateEvent;
import com.notification_service.kafka.dto.UserRegistrationEvent;

public interface EmailService {

    void sendWelcomeEmail(UserRegistrationEvent event);

    void sendTransactionAlertEmail(BalanceUpdateEvent event);


}
