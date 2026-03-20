package com.example.paymentsystem.domain.payment.dto;

public record PortOnePaymentResponse(Integer code, String message, PaymentDetail response) {

    public record PaymentDetail(String imp_uid, String merchant_uid, Long amount, String status) {}
}
