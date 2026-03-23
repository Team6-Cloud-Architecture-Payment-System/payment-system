package com.example.paymentsystem.domain.payment.dto;

public record PortOnePaymentResponse(
        String id,
        String status,
        AmountDetails amount,
        OrderDetails order
) {
    public record AmountDetails(Long total) {}
    public record OrderDetails(String name) {}
}