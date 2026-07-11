package com.user_account_service_service.service.impl;

import com.user_account_service_service.dto.*;
import com.user_account_service_service.entity.Account;
import com.user_account_service_service.entity.Role;
import com.user_account_service_service.entity.User;
import com.user_account_service_service.enums.AccountStatus;
import com.user_account_service_service.enums.AccountType;
import com.user_account_service_service.enums.Currency;
import com.user_account_service_service.exceptions.BadRequestException;
import com.user_account_service_service.exceptions.NotFoundException;
import com.user_account_service_service.kafka.dto.UserRegistrationEvent;
import com.user_account_service_service.kafka.service.AccountEventPublisher;
import com.user_account_service_service.repository.AccountRepository;
import com.user_account_service_service.repository.RoleRepository;
import com.user_account_service_service.repository.UserRepository;
import com.user_account_service_service.security.JwtService;
import com.user_account_service_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AccountEventPublisher accountEventPublisher;

    @Override
    public ApiResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest) {
        log.info("We are inside the register user service method");
        if(userRepository.existsByEmail(registrationRequest.getEmail())){
            throw new BadRequestException("Account already exist for this email");
        }

        Set<Role> roles = new HashSet<>();

        String roleName = (registrationRequest.getRole() != null && !registrationRequest.getRole().isBlank())
                ? registrationRequest.getRole().toUpperCase()
                : "CUSTOMER";

        Role databaseRole = roleRepository.findByName(roleName)
                .orElseThrow(()-> new NotFoundException("Role with name" + roleName + " Not found"));

        roles.add(databaseRole);
        User userToSave = User.builder()
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .enabled(true)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(userToSave);

        String accountNumber = generateUniqueAccountNumber();

        Account accountToSaveToDb = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .currency(Currency.VND)
                .accountType(AccountType.SAVINGS)
                .accountStatus(AccountStatus.ACTIVE)
                .user(savedUser)
                .build();
        accountRepository.save(accountToSaveToDb);

        UserRegistrationEvent event = UserRegistrationEvent.builder()
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .accountNumber(accountNumber)
                .bankName("BANK NOW")
                .build();
        accountEventPublisher.publishedUserRegistrationEvent(event);

        UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .user(userDTO)
                .build();
        return new ApiResponse<>(HttpStatus.CONTINUE.value(), "User account created successfully", authResponse);
    }

    @Override
    public ApiResponse<AuthResponse> loginUser(LoginRequest loginRequest) {
        log.info("Inside login service method");
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new NotFoundException("User not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("Password doesn't match");
        }
        if(!user.isEnabled()){
            throw new BadRequestException("User is disabled");
        }

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        String token = jwtService.generateToken(user.getEmail(), roles);

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .user(userDTO)
                .build();

        return new ApiResponse<>(
                HttpStatus.OK.value(),
                "login successful",
                authResponse
        );
    }

    private String generateUniqueAccountNumber(){
        String accountNumber;

        ThreadLocalRandom random = ThreadLocalRandom.current();

        do{
            //it will generate random 8 digits(between 0000000 to 99999999)
            int randomPart = random.nextInt(100_000_000);
            accountNumber = String.format("00%08d", randomPart);

        }while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}
