package com.user_account_service_service.controller;

import com.user_account_service_service.dto.ApiResponse;
import com.user_account_service_service.dto.UserDTO;
import com.user_account_service_service.dto.UserStatisticsDTO;
import com.user_account_service_service.dto.UserWithAccountDTO;
import com.user_account_service_service.service.UserService;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

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
@Tag(name = "Admin - User", description = "APIs quản trị người dùng, chỉ dành cho người dùng có quyền ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/search")
    @Operation(
            summary = "Tìm kiếm người dùng theo email hoặc số tài khoản",
            description = "Tìm một người dùng cùng thông tin tài khoản đi kèm, dựa trên email hoặc accountNumber. " +
                    "Truyền ít nhất 1 trong 2 tham số. Yêu cầu quyền ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Tìm thấy người dùng phù hợp"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Không truyền email lẫn accountNumber"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Người dùng không có quyền ADMIN"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy người dùng phù hợp"
            )
    })
    public ResponseEntity<ApiResponse<UserWithAccountDTO>> search(
            @Parameter(description = "Email của người dùng cần tìm", example = "hung.nguyen@gmail.com")
            @RequestParam(required = false) String email,

            @Parameter(description = "Số tài khoản của người dùng cần tìm", example = "1234567890")
            @RequestParam(required = false) String accountNumber
    ) {
        return ResponseEntity.ok(userService.searchUser(email, accountNumber));
    }

    @GetMapping("/all")
    @Operation(
            summary = "Lấy danh sách tất cả người dùng (phân trang)",
            description = "Trả về danh sách người dùng trong hệ thống, hỗ trợ phân trang và lọc theo role. " +
                    "Yêu cầu quyền ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách người dùng thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Người dùng không có quyền ADMIN"
            )
    })
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
            @Parameter(description = "Lọc người dùng theo tên role (VD: ADMIN, USER)", example = "USER")
            @RequestParam(required = false) String roleName,

            @ParameterObject
            @PageableDefault(page = 0, size = 100) Pageable pageable
    ) {
        return ResponseEntity.ok(userService.getAllUser(roleName, pageable));
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Lấy thống kê người dùng",
            description = "Trả về các số liệu thống kê tổng quan về người dùng trong hệ thống " +
                    "(VD: tổng số người dùng, số lượng đang active...). Yêu cầu quyền ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy thống kê thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Người dùng không có quyền ADMIN"
            )
    })
    public ResponseEntity<ApiResponse<UserStatisticsDTO>> getStatistics() {
        return ResponseEntity.ok(userService.getUserStatistics());
    }

    @PatchMapping("/toggle-status/{userId}")
    @Operation(
            summary = "Bật/tắt trạng thái hoạt động của người dùng",
            description = "Đảo trạng thái active/inactive của một người dùng theo userId. Yêu cầu quyền ADMIN."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật trạng thái thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Người dùng không có quyền ADMIN"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy người dùng với userId đã cho"
            )
    })
    public ResponseEntity<ApiResponse<String>> toggleStatus(
            @Parameter(description = "ID của người dùng cần đổi trạng thái", required = true, example = "10")
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(userService.toggleUserStatus(userId));
    }
}