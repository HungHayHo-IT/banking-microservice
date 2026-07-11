package com.user_account_service_service.repository;

import com.user_account_service_service.entity.Account;
import com.user_account_service_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber );

    Optional<Account> findByUser(User user);
}
