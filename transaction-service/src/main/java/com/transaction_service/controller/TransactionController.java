package com.transaction_service.controller;

import com.transaction_service.dto.ApiResponse;
import com.transaction_service.dto.TransactionDTO;
import com.transaction_service.dto.TransactionRequest;
import com.transaction_service.enums.TransactionDirection;
import com.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@Tag(name = "Transaction", description = "APIs thực hiện và tra cứu giao dịch của người dùng đang đăng nhập")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    @Operation(
            summary = "Chuyển khoản giữa 2 tài khoản",
            description = "Thực hiện chuyển tiền từ fromAccountNumber sang toAccountNumber. Cả 2 đều bắt buộc. " +
                    "Sau khi xử lý thành công, hệ thống sẽ publish sự kiện Kafka (balance-update-events) để cập nhật số dư cả 2 tài khoản."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Chuyển khoản thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ, hoặc số dư không đủ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản nguồn hoặc tài khoản đích")
    })
    public ResponseEntity<ApiResponse<TransactionDTO>> transfer(
            @Valid @RequestBody TransactionRequest request,
            @Parameter(description = "Correlation ID dùng để tracing request giữa các service", required = true)
            @RequestHeader("banknow-correlation-id") String correlationId
    ) {
        logger.debug("bankapp-correlation-id found: {}", correlationId);
        return ResponseEntity.ok(transactionService.transfer(request, correlationId));
    }

    @PostMapping("/withdraw")
    @Operation(
            summary = "Rút tiền từ tài khoản",
            description = "Thực hiện rút tiền từ fromAccountNumber. Sau khi xử lý thành công, hệ thống sẽ publish " +
                    "sự kiện Kafka (balance-update-events) để cập nhật số dư."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rút tiền thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ, hoặc số dư không đủ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản nguồn")
    })
    public ResponseEntity<ApiResponse<TransactionDTO>> withdraw(
            @Valid @RequestBody TransactionRequest request,
            @Parameter(description = "Correlation ID dùng để tracing request giữa các service", required = true)
            @RequestHeader("banknow-correlation-id") String correlationId
    ) {
        logger.debug("bankapp-correlation-id found: {}", correlationId);
        return ResponseEntity.ok(transactionService.withdraw(request, correlationId));
    }

    @GetMapping("/history")
    @Operation(
            summary = "Tra cứu lịch sử giao dịch theo khoảng thời gian",
            description = "Trả về danh sách giao dịch của một tài khoản trong khoảng [start, end]. " +
                    "Nếu 'start' bỏ trống, mặc định lấy từ 2026-01-01. Nếu 'end' bỏ trống, mặc định lấy đến thời điểm hiện tại."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy lịch sử giao dịch thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản với accountNumber đã cho")
    })
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getHistory(
            @Parameter(description = "Số tài khoản cần tra cứu lịch sử", required = true, example = "1234567890")
            @RequestParam String accountNumber,

            @Parameter(description = "Ngày bắt đầu (định dạng yyyy-MM-dd). Mặc định 2026-01-01 nếu bỏ trống", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,

            @Parameter(description = "Ngày kết thúc (định dạng yyyy-MM-dd). Mặc định là thời điểm hiện tại nếu bỏ trống", example = "2026-07-12")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        // convert the start date to LocalDateTime if it is provided else assign January 2026 as the start day time
        LocalDateTime startDate = (start != null)
                ? start.atStartOfDay()
                : LocalDateTime.of(2026, 1, 1, 0, 0);

        LocalDateTime endDate = (end != null)
                ? end.atTime(LocalTime.MAX)
                : LocalDateTime.now();

        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber, startDate, endDate));
    }

    @GetMapping("/history/direction")
    @Operation(
            summary = "Tra cứu lịch sử giao dịch theo chiều giao dịch",
            description = "Trả về danh sách giao dịch của một tài khoản, lọc theo chiều CREDIT (ghi có) hoặc DEBIT (ghi nợ)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy lịch sử giao dịch thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy tài khoản với accountNumber đã cho")
    })
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getHistoryByDirection(
            @Parameter(description = "Số tài khoản cần tra cứu lịch sử", required = true, example = "1234567890")
            @RequestParam String accountNumber,

            @Parameter(description = "Chiều giao dịch cần lọc", required = true, example = "CREDIT",
                    schema = @Schema(implementation = TransactionDirection.class))
            @RequestParam TransactionDirection direction
    ) {
        return ResponseEntity.ok(transactionService.getMyTransactionHistoryByDirection(accountNumber, direction));
    }

    @GetMapping("/reference/{reference}")
    @Operation(
            summary = "Tra cứu giao dịch theo mã tham chiếu",
            description = "Trả về thông tin chi tiết một giao dịch dựa trên mã tham chiếu (reference) duy nhất."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tìm thấy giao dịch"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Người dùng chưa đăng nhập hoặc token không hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch với reference đã cho")
    })
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionByReference(
            @Parameter(description = "Mã tham chiếu giao dịch", required = true, example = "TXN-20260712-0001")
            @PathVariable String reference
    ) {
        return ResponseEntity.ok(transactionService.getTransactionByReference(reference));
    }
}