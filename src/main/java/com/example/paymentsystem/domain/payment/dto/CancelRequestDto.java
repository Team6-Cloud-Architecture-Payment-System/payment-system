package com.example.paymentsystem.domain.payment.dto;

public record CancelRequestDto(
        String reason  //환불사유(필수)
) {}