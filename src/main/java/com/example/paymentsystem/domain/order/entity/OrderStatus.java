package com.example.paymentsystem.domain.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PAYMENT_PENDING("결제 대기"),
    ORDER_COMPLETED("주문 완료"),
    ORDER_CONFIRMED("주문 확정"),
    REFUNDED("환불 완료"),
    FAIL("주문 실패");

    private final String statusName;

    OrderStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }
}
