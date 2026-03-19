package com.example.paymentsystem.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogOutRequest (
        @NotBlank String refreshToken
) {
}
