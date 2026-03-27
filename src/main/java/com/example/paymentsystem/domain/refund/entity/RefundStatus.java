package com.example.paymentsystem.domain.refund.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefundStatus {
    REFUND_REQUESTED("환불 요청"),
    REFUND_COMPLETED("환불 완료"),
    REFUND_FAILED("환불 실패");

    private final String message;


}
