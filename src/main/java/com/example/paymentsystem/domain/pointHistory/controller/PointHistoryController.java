package com.example.paymentsystem.domain.point.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.point.dto.GetPointTransactionHistory;
import com.example.paymentsystem.domain.point.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointHistoryController {
    private final PointHistoryService pointHistoryService;

    // 현재 내 포인트 조회
//    @GetMapping("/me")
//    public ResponseEntity<ApiResponse<GetMyPointHistoryResponse>> getMyPoint(
//            @RequestParam Long userId
//    ) {
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(pointHistoryService.getMyPoint(userId)));
//    }
    @GetMapping("/me/history")
    public ResponseEntity<ApiResponse<Page<GetPointTransactionHistory>>> getPointTransactionHistory(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page -1, size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(pointHistoryService.getPointTransactionHistory(userId, pageable)));
    }


}
