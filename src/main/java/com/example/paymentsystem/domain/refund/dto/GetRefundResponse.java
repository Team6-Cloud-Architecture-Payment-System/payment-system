package com.example.paymentsystem.domain.refund.dto;

import com.example.paymentsystem.domain.refund.entity.Refund;
import com.example.paymentsystem.domain.refund.entity.RefundStatus;

import java.time.LocalDateTime;

public record GetRefundResponse (
    Long id,
    Long orderId,
    Long refundPrice,
    String refundReason,
    RefundStatus status,
    LocalDateTime refundCreatedAt
){
    public GetRefundResponse(Refund refund) {
        this(
                refund.getId(),
                refund.getPayment().getOrderId(),
                refund.getRefundPrice(),
                refund.getRefundReason(),
                refund.getStatus(),
                refund.getRefundCreatedAt()
                );
    }
}


