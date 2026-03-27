package com.example.paymentsystem.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CancelResponseDto(
        Cancellation cancellation
) {
    public record Cancellation(
            String status,          // REQUESTED / SUCCEEDED / FAILED

            @JsonProperty("id")
            String cancellationId,  // 취소 내역 아이디

            int totalAmount,        // 환불 금액
            String reason,          // 환불 사유
            String cancelledAt,     // 환불 처리 시각
            String requestedAt      // 환불 요청 시각
    ) {}
}