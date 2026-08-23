package com.user_account_service_service.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisteredEvent {

    private UUID eventId;
    private Instant occurredAt;

    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String bankName;
}