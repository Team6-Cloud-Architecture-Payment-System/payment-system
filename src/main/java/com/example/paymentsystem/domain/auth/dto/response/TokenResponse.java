package com.example.paymentsystem.domain.auth.dto.response;

public record TokenResponse (
        String accessToken,
        String refreshToken,
        String grantType
) {
}
