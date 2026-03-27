package com.example.paymentsystem.domain.auth.dto.response;

import com.example.paymentsystem.domain.auth.entity.User;

public record SignUpResponse(
        Long id,
        String email,
        String name,
        String phoneNumber
) {
    public static SignUpResponse from (User user) {
        return new SignUpResponse(user.getId(), user.getEmail(), user.getName(), user.getPhoneNumber());
    }
}
