package com.example.paymentsystem.domain.payment.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.payment.dto.PaymentConfirmRequest;
import com.example.paymentsystem.domain.payment.dto.PaymentTryRequest;
import com.example.paymentsystem.domain.payment.dto.WebhookRequestDto;
import com.example.paymentsystem.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/api/payments/webhook")
    public ResponseEntity<Void> webhook(
            @RequestHeader("webhook-id") String webhookId,
            @RequestBody WebhookRequestDto request
    ) {
        //포트원연동 후 보내준 데이터 확인하기
        log.info("webhookId: {}", webhookId);
        log.info("request: {}", request);

        paymentService.receiveWebhook(request, webhookId);

        return ResponseEntity.ok().build();

    }

    @PostMapping("/api/orders/{orderId}/payments")
    public ResponseEntity<ApiResponse> tryPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentTryRequest request
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(paymentService.tryPayment(orderId, request)));
    }

    @PatchMapping("/api/orders/{orderId}/payments/confirm")
    public ResponseEntity<ApiResponse<Void>> completePayment(@RequestBody PaymentConfirmRequest request) {
        ApiResponse.success(paymentService.confirmPayment(request));
        return ResponseEntity.ok().build();
    }
}
