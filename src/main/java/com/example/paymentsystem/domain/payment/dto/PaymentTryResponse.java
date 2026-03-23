package com.example.paymentsystem.domain.payment.dto;

import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;

public record PaymentTryResponse(
        Long orderId,
        PaymentStatus paymentStatus,
        String paymentId,
        Long paymentPrice
        ) {

    public PaymentTryResponse(Payment payment) {
        this(
                payment.getOrder().getId(),
                payment.getPaymentStatus(),
                payment.getPaymentId(),
                payment.getPaymentPrice()
        );
    }
}
