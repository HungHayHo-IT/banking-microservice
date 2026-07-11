package com.user_account_service_service.controller;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.UserWithAccountDTO;
import com.user_account_service_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserWithAccountDTO>> getMyProfile(){
        return ResponseEntity.ok(userService.getMyDetails());
    }
}
