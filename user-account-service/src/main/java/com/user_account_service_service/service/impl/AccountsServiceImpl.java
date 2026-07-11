package com.user_account_service_service.service.impl;

import com.user_account_service_service.config.ModelMapperConfig;
import com.user_account_service_service.dto.AccountDTO;
import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.entity.Account;
import com.user_account_service_service.entity.User;
import com.user_account_service_service.enums.AccountStatus;
import com.user_account_service_service.exceptions.NotFoundException;
import com.user_account_service_service.repository.AccountRepository;
import com.user_account_service_service.repository.UserRepository;
import com.user_account_service_service.service.AccountsService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
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
    public ApiResponse<AccountDTO> getAccountNumber(String accountNumber) {

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
}
