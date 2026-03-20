package com.example.paymentsystem.domain.auth.dto.response;

import com.example.paymentsystem.domain.auth.entity.User;

public record UserInfoResponse(
        Long id,
        String name,
        String email,
        String phoneNumber,
        Long point
) {
    public static UserInfoResponse from (User user) {
        return new UserInfoResponse(user.getId(), user.getName(),  user.getEmail(), user.getPhoneNumber(), user.getPoint());
    }
}
