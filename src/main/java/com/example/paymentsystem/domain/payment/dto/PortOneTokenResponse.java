package com.example.paymentsystem.domain.payment.dto;

public record PortOneTokenResponse(Integer code, String message, TokenDetail response) {
    public record TokenDetail(String access_token, Integer now, Integer expired_at) {}
}
