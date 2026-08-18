package com.user_account_service_service.service.impl;

import com.user_account_service_service.config.ModelMapperConfig;
import com.user_account_service_service.dto.*;
import com.user_account_service_service.entity.Account;
import com.user_account_service_service.entity.User;
import com.user_account_service_service.enums.AccountStatus;
import com.user_account_service_service.exceptions.BadRequestException;
import com.user_account_service_service.exceptions.NotFoundException;
import com.user_account_service_service.repository.AccountRepository;
import com.user_account_service_service.repository.UserRepository;
import com.user_account_service_service.service.AccountsService;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountsServiceImpl implements AccountsService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<AccountDTO> getMyAccount() {
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication().getName());

        User user = userRepository.findByEmail(email).orElseThrow(()->new NotFoundException("not found user"));

        Account account = accountRepository.findByUser(user).orElseThrow(()->new NotFoundException("not account for you"));

        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);

        accountDTO.setOwnerEmail(account.getUser().getEmail());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account Retrieved",
                accountDTO
        );
    }

    @Override
    public ApiResponse<AccountDTO> getAccountNumber(String accountNumber ,String correlationId) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new NotFoundException("Account Not Found"));

        AccountDTO accountDTO = modelMapper.map(account, AccountDTO.class);

        accountDTO.setOwnerEmail(account.getUser().getEmail());

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account Retrieved",
                accountDTO
        );

    }

    @Override
    public ApiResponse<AccountDTO> changeAccountStatus(String accountNumber, AccountStatus status) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new NotFoundException("Account Not Found"));

        account.setAccountStatus(status);
        Account savedAccount = accountRepository.save(account);


        AccountDTO accountDTO = modelMapper.map(savedAccount, AccountDTO.class);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account Status Updated Successfully",
                accountDTO
        );
    }
    @Override
    public ApiResponse<Page<AccountDTO>> getAllAccounts(Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );

        Page<Account> accounts = accountRepository.findAll(sortedPageable);

        Page<AccountDTO> dtoPage = accounts.map(account -> modelMapper.map(account, AccountDTO.class));

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Accounts Retrieved",
                dtoPage
        );
    }

    @Override
    @Transactional
    public ApiResponse<InternalTransferResponse> transfer(
            InternalTransferRequest request,
            String correlationId) {

        log.info(
                "Processing internal transfer. reference={}, correlationId={}",
                request.getReference(),
                correlationId
        );

        if (request.getFromAccountNumber()
                .equals(request.getToAccountNumber())) {
            throw new BadRequestException(
                    "Source and destination accounts must be different"
            );
        }

        /*
         * Luôn khóa tài khoản theo cùng một thứ tự.
         *
         * Ví dụ:
         * - Request 1 chuyển A -> B
         * - Request 2 chuyển B -> A
         *
         * Nếu mỗi request khóa theo thứ tự khác nhau thì có nguy cơ deadlock.
         */
        String firstNumber =
                request.getFromAccountNumber()
                        .compareTo(request.getToAccountNumber()) < 0
                        ? request.getFromAccountNumber()
                        : request.getToAccountNumber();

        String secondNumber =
                firstNumber.equals(request.getFromAccountNumber())
                        ? request.getToAccountNumber()
                        : request.getFromAccountNumber();

        Account firstLocked =
                accountRepository.findByAccountNumberForUpdate(firstNumber)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Account " + firstNumber + " not found"
                                ));

        Account secondLocked =
                accountRepository.findByAccountNumberForUpdate(secondNumber)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Account " + secondNumber + " not found"
                                ));

        Account source =
                firstLocked.getAccountNumber()
                        .equals(request.getFromAccountNumber())
                        ? firstLocked
                        : secondLocked;

        Account destination =
                source == firstLocked
                        ? secondLocked
                        : firstLocked;

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new ForbiddenException(
                    "Authentication is required"
            );
        }

        if (!source.getUser().getEmail()
                .equalsIgnoreCase(authentication.getName())) {
            throw new ForbiddenException(
                    "You are not the owner of the source account"
            );
        }

        if (source.getAccountStatus() != AccountStatus.ACTIVE
                || destination.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException(
                    "Both accounts must be ACTIVE"
            );
        }

        if (source.getCurrency() != destination.getCurrency()) {
            throw new BadRequestException(
                    "Accounts must use the same currency"
            );
        }

        if (source.getBalance()
                .compareTo(request.getAmount()) < 0) {
            throw new BadRequestException(
                    "Insufficient account balance"
            );
        }

        source.setBalance(
                source.getBalance().subtract(request.getAmount())
        );

        destination.setBalance(
                destination.getBalance().add(request.getAmount())
        );

        accountRepository.saveAll(
                List.of(source, destination)
        );

        InternalTransferResponse result =
                InternalTransferResponse.builder()
                        .debitAccount(toSnapshot(source))
                        .creditAccount(toSnapshot(destination))
                        .build();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "Balances updated",
                result
        );
    }

    private AccountBalanceSnapshot toSnapshot(Account account) {
        return AccountBalanceSnapshot.builder()
                .accountNumber(account.getAccountNumber())
                .email(account.getUser().getEmail())
                .firstName(account.getUser().getFirstName())
                .currentBalance(account.getBalance())
                .build();
    }
}
