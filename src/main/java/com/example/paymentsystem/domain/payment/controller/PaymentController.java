package com.example.paymentsystem.domain.payment.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.payment.dto.PaymentTryResponse;
import com.example.paymentsystem.domain.payment.dto.WebhookRequestDto;
import com.example.paymentsystem.domain.payment.service.PaymentService;
import com.example.paymentsystem.domain.payment.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;
    private final WebhookService webhookService;

    @PostMapping("/api/payments/webhook")
    public ResponseEntity<Void> webhook(
            @RequestHeader(value="webhook-id", required = false) String webhookId,
            @RequestBody WebhookRequestDto request
    ) {
        //포트원연동 후 보내준 데이터 확인하기
        log.info("webhookId: {}", webhookId);
        log.info("request: {}", request);

        webhookService.receiveWebhook(request, webhookId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/api/orders/{orderId}/payments")
    public ResponseEntity<ApiResponse<PaymentTryResponse>> tryPayment(
            @PathVariable Long orderId
            ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(paymentService.tryPayment(orderId)));
    }

    @PostMapping("/api/payments/{paymentId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(@PathVariable String paymentId) {
        paymentService.confirmPayment(paymentId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("결제 완료되었습니다."));
    }
}
