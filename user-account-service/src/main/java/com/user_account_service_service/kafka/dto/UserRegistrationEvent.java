package com.user_account_service_service.kafka.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationEvent {
    private String email;
    private String firstName;
    private String lastName;
    private String accountNumber;
    private String bankName = "BANK NOW";
}
