package com.example.paymentsystem.domain.payment.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.payment.dto.*;
import com.example.paymentsystem.domain.payment.service.PaymentService;
import com.example.paymentsystem.domain.payment.service.PortOneService;
import com.example.paymentsystem.domain.payment.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
    private final PortOneService portOneService;
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

        return ResponseEntity.ok().build();

    }

    @PostMapping("/api/orders/{orderId}/payments")
    public ResponseEntity<ApiResponse> tryPayment(
            @PathVariable Long orderId
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(paymentService.tryPayment(orderId)));
    }

    @PostMapping("/api/payments/{paymentId}/confirm")
    public ResponseEntity<Void> completePayment(@PathVariable String paymentId) {
        paymentService.confirmPayment(paymentId);
        return ResponseEntity.ok().build();
    }

    //포트원 검증 엔드포인트
    @GetMapping("/api/payments/{paymentId}/verify")
    public ResponseEntity<ApiResponse> verifyPayment(@PathVariable String paymentId) {

        validatePaymentId(paymentId);

        PortOneVerificationResponseDto response = portOneService.getVerifyPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    //포트원 결제 취소 엔드포인트
    @PostMapping("/api/payments/{paymentId}/cancel")
    public ResponseEntity<ApiResponse> cancelPayment(
            @PathVariable String paymentId,
            @RequestBody CancelRequestDto request
    ) {
        validatePaymentId(paymentId);
        CancelResponseDto response = portOneService.cancelPayment(paymentId, request);

        if ("SUCCEEDED".equals(response.cancellation().status())) {
            log.info("취소 완료 시각: {}", response.cancellation().cancelledAt());
        }

        log.info("포트원 취소 response: {}", response.cancellation().totalAmount());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private void validatePaymentId(String paymentId) {
        //hasText() - null, 빈문자열, 공백 모두 처리
        if (!StringUtils.hasText(paymentId)) {
            throw new IllegalArgumentException("paymentId가 없습니다.");
        }
    }
}
