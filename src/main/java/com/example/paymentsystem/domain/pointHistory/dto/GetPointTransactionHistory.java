package com.example.paymentsystem.domain.point.dto;

import com.example.paymentsystem.domain.point.entity.Type;

import java.time.LocalDateTime;

public record GetPointTransactionHistory (
        Long id,
        Long orderId,
        Long point,
        Type type,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
){}
