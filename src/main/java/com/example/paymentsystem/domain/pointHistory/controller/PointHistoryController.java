package com.example.paymentsystem.domain.pointHistory.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.pointHistory.dto.GetPointTransactionHistory;
import com.example.paymentsystem.domain.pointHistory.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointHistoryController {
    private final PointHistoryService pointHistoryService;

    // 포인트 거래 내역 조회
    @GetMapping("/me/history")
    public ResponseEntity<ApiResponse<GetPointTransactionHistory>> getPointTransactionHistory(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page -1, size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(pointHistoryService.getPointTransactionHistory(userId, pageable)));
    }


}
