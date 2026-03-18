package com.example.paymentsystem.domain.payment.dto;

public record WebhookData(
        String storeId,
        String paymentId,
        String transactionId,
        String cancellationId,//Optional
        String eventStatus
) {}
