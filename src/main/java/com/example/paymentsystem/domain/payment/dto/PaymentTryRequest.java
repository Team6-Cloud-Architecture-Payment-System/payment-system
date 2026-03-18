package com.example.paymentsystem.domain.payment.dto;

public record PaymentTryRequest(
        Long orderId,
        Long payments_id,
        Long payment_price) {
}
