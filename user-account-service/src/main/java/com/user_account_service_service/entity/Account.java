package com.user_account_service_service.entity;

import com.user_account_service_service.enums.AccountStatus;
import com.user_account_service_service.enums.AccountType;
import com.user_account_service_service.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Currency currency; // USD, EURO

    @Enumerated(EnumType.STRING)
    private AccountType accountType; //SAVINGS, CURRENT

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus; //ACTIVE, CLOSED, FROZEN


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private final LocalDateTime createdAt = LocalDateTime.now();
}