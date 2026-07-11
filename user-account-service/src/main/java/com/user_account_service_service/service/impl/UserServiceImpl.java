package com.user_account_service_service.service.impl;

import com.user_account_service_service.dto.*;
import com.user_account_service_service.entity.Account;
import com.user_account_service_service.entity.User;
import com.user_account_service_service.exceptions.BadRequestException;
import com.user_account_service_service.exceptions.NotFoundException;
import com.user_account_service_service.repository.AccountRepository;
import com.user_account_service_service.repository.UserRepository;
import com.user_account_service_service.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<UserWithAccountDTO> getMyDetails() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Inside getuserdetails user email from authentication is: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(
                ()-> new NotFoundException("User not found")
        );

        Account account = accountRepository.findByUser(user).orElseThrow(
                ()-> new NotFoundException("Account not found")
        );

        UserWithAccountDTO userWithAccountDTO = mapToUserWithAccountDto(user,account);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "profile retreived",
                userWithAccountDTO
        );

    }

    @Override
    public ApiResponse<UserWithAccountDTO> searchUser(String email, String accountNumber) {
        log.info("searching for a user");

        User user;
        Account account;

        if (email != null && !email.isBlank()){

            user = userRepository.findByEmail(email)
                    .orElseThrow(()-> new NotFoundException("User with email not found"));

            account = accountRepository.findByUser(user)
                    .orElseThrow(()-> new NotFoundException("No Account For you"));


        }else if(accountNumber != null && !accountNumber.isBlank()){

            account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(()-> new NotFoundException("No Account For you"));
            user = account.getUser();
        }else{
            throw  new BadRequestException("And Email or Account Number is Required");
        }


        UserWithAccountDTO userWithAccountDTO = mapToUserWithAccountDto(user, account);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "profile retreived successfully",
                userWithAccountDTO);


    }

    @Override
    public ApiResponse<Page<UserDTO>> getAllUser(String roleName, Pageable pageable) {
        log.info("Getting all users");

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );

        Page<User> userPage;
        if (roleName != null && !roleName.isBlank()){
            userPage = userRepository.findByRoleName(roleName.toUpperCase(), sortedPageable);
        }else{
            userPage = userRepository.findAll(sortedPageable);
        }

        Page<UserDTO> dtoPage = userPage.map(user -> modelMapper.map(user, UserDTO.class));
        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "users fetched successfully",
                dtoPage
        );
    }

    @Override
    public ApiResponse<UserStatisticsDTO> getUserStatistics() {

        long total = userRepository.count();
        long enabled = userRepository.countByEnabledTrue();

        UserStatisticsDTO stats = UserStatisticsDTO.builder()
                .totalUsers(total)
                .activeUsers(enabled)
                .inactiveUsers(total - enabled)
                .totalAccounts(accountRepository.count())
                .averageAccountPerUser(accountRepository.count())
                .customersCount(userRepository.countByRoleName("CUSTOMER"))
                .adminsCount(userRepository.countByRoleName("ADMIN"))
                .build();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "statistics fetched successfully",
                stats
        );
    }

    @Override
    public ApiResponse<String> toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User with ID Not Found"));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);

        String status = user.isEnabled() ? "Enabled" : "Disabled";
        log.info("User has been {}", status);

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "User Status Changed to: " + status,
                null
        );

    }

    private UserWithAccountDTO mapToUserWithAccountDto(User user , Account account){
        return UserWithAccountDTO.builder()
                .user(modelMapper.map(user,UserDTO.class))
                .account(modelMapper.map(account, AccountDTO.class))
                .build();
    }
}
