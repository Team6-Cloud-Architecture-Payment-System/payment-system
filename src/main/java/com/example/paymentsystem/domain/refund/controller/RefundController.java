package com.example.paymentsystem.domain.refund.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.refund.dto.CreateRefundRequest;
import com.example.paymentsystem.domain.refund.dto.CreateRefundResponse;
import com.example.paymentsystem.domain.refund.dto.GetMyRefundListResponse;
import com.example.paymentsystem.domain.refund.dto.GetOrderRefundResponse;
import com.example.paymentsystem.domain.refund.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RefundController {
    private final RefundService refundService;

    // 환불 요청
    @PostMapping("/payments/{paymentId}/refunds")
    public ResponseEntity<ApiResponse<CreateRefundResponse>> createRefundRequest(
            @PathVariable String paymentId,
            @Valid @RequestBody CreateRefundRequest request,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(refundService.createRefundRequest(paymentId, request, userId)));
    }

    // @AuthenticationPrincipal >> SecurityContextHolder에 저장된 JWT 토큰을 꺼냄,
    // 로그인한 인증된 유저의 정보를 꺼내서 주입받을 때 사용, Session방식에서 @SessionAttribute와 유사함

    // 특정 주문 환불 내역 조회
    @GetMapping("/orders/{orderId}/refunds")
    public ResponseEntity<ApiResponse<GetOrderRefundResponse>> getRefund(
            @PathVariable Long orderId,
            @AuthenticationPrincipal Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(refundService.getRefund(orderId, userId)));
    }

    // 유저 개인의 환불내역 전체 조회
    @GetMapping("/refunds/me")
    public ResponseEntity<ApiResponse<GetMyRefundListResponse>> getMyRefundList(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page -1, size);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(refundService.getMyRefundList(userId, pageable)));
    }

}
