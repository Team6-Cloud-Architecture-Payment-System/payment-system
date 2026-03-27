package com.example.paymentsystem.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogInRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}
