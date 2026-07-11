package com.user_account_service_service.controller;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.UserDTO;
import com.user_account_service_service.dto.UserStatisticsDTO;
import com.user_account_service_service.dto.UserWithAccountDTO;
import com.user_account_service_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

    private final UserService userService;


    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserWithAccountDTO>> search(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String accountNumber
    ) {
        return ResponseEntity.ok(userService.searchUser(email, accountNumber));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
            @RequestParam(required = false) String roleName,
            @PageableDefault(page = 0, size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUser(roleName, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatisticsDTO>> getStatistics(){
        return ResponseEntity.ok(userService.getUserStatistics());
    }

    @PatchMapping("/toggle-status/{userId}")
    public ResponseEntity<ApiResponse<String>> toggleStatus(@PathVariable Long userId){
        return ResponseEntity.ok(userService.toggleUserStatus(userId));
    }
}
