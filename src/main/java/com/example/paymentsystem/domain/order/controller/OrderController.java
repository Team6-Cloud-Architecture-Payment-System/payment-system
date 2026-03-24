package com.example.paymentsystem.domain.order.controller;
import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.order.dto.CreateOrderRequest;
import com.example.paymentsystem.domain.order.dto.CreateOrderResponse;
import com.example.paymentsystem.domain.order.dto.OrderDetailResponse;
import com.example.paymentsystem.domain.order.dto.OrderHistoryResponse;
import com.example.paymentsystem.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>> create(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderService.create(userId, request)));
    }

    // 주문 내역 조회
    @GetMapping
    public ResponseEntity<ApiResponse<OrderHistoryResponse>> getMyOrders(
            @AuthenticationPrincipal Long userId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return ResponseEntity.ok(
                ApiResponse.success(orderService.getMyOrders(userId, sortedPageable))
        );
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(orderService.getOrderDetail(userId, orderId))
        );
    }


    // 주문 확정
    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<?>> confirmOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId
    ) {
        orderService.confirmOrder(userId, orderId);

        return ResponseEntity.ok(
                ApiResponse.success("주문이 확정되었습니다.")
        );
    }
}

