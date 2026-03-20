package com.example.paymentsystem.domain.payment.dto;

import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;

public record PaymentTryResponse(
        Long id,
        Long orderId,
        PaymentStatus paymentStatus,
        String paymentId,
        Long paymentPrice
        ) {

    public PaymentTryResponse(Payment payment) {
        this(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentStatus(),
                payment.getPaymentId(),
                payment.getPaymentPrice()
        );
    }
}
