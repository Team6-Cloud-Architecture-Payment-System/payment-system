package com.example.paymentsystem.domain.payment.dto;

public record WebhookRequestDto(
       String type,
       WebhookData data
){}
