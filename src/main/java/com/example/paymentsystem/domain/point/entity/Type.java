package com.example.paymentsystem.domain.point.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Type {
    EARNED("적립"),
    SPENT("사용"),
    RESTORED("복구"),
    CANCELLED("취소"),
    EXPIRED("소멸"),
    ADMIN("관리자 조정");

    private final String status;
}
