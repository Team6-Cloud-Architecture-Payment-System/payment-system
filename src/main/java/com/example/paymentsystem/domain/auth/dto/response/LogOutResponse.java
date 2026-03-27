package com.example.paymentsystem.domain.auth.dto.response;

public record LogOutResponse(
        boolean success,
        String message
) {}