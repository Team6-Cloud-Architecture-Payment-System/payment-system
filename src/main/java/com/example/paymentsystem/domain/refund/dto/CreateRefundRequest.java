package com.example.paymentsystem.domain.refund.dto;

public record CreateRefundRequest(
    Long refundPrice,
    String refundReason
){}
