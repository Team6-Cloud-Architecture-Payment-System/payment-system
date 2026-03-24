package com.example.paymentsystem.domain.payment.entity;

public enum PaymentStatus {
    PENDING("대기"),
    PAID("결제 완료"),
    FAIL("실패"),
    REFUNDED("환불 완료");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }
}
