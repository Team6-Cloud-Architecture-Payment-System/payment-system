package com.example.paymentsystem.domain.refund.dto;

public record CreateRefundRequest(

        Long paymentId,
        Long refundPrice,
        String refundReason
) {
}
