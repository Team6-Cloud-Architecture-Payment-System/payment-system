package com.example.paymentsystem.domain.payment.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.payment.dto.PaymentTryRequest;
import com.example.paymentsystem.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/api/orders/{orderId}/payments")
    public ResponseEntity<ApiResponse> tryPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentTryRequest request
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(paymentService.tryPayment(orderId, request)));
    }
}
