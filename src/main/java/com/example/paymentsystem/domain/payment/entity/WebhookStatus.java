package com.example.paymentsystem.domain.payment.entity;

public enum WebhookStatus {
    RECEIVED("웹훅 수신"),
    PROCESSED("처리 완료"),
    FAILED("처리 실패");

    private final String description;

    WebhookStatus(String description) {
        this.description = description;
    }
}
