package com.example.paymentsystem.domain.payment.dto;

import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;

public record PaymentTryResponse(
        Long id,
        Long orderId,
        PaymentStatus payment_status,
        Long payment_price
        ) {

    public PaymentTryResponse(Payment payment) {
        this(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPayment_status(),
                payment.getPayment_price()
        );
    }
}
