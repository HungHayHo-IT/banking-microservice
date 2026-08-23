package com.notification_service.service;

import com.notification_service.kafka.dto.BalanceUpdateEvent;
import com.notification_service.kafka.dto.UserRegisteredEvent;

public interface EmailService {

    void sendWelcomeEmail(UserRegisteredEvent event);

    void sendTransactionAlertEmail(BalanceUpdateEvent event);


}
