package com.example.paymentsystem.domain.refund.dto;

import com.example.paymentsystem.domain.refund.entity.Refund;
import com.example.paymentsystem.domain.refund.entity.RefundStatus;

import java.time.LocalDateTime;

public record GetOrderRefundResponse(
    Long id,
    Long orderId,
    Long refundPrice,
    String refundReason,
    RefundStatus status,
    LocalDateTime createdAt
){
    public GetOrderRefundResponse(Refund refund) {
        this(
                refund.getId(),
                refund.getPayment().getOrder().getId(),
                refund.getRefundPrice(),
                refund.getRefundReason(),
                refund.getStatus(),
                refund.getCreatedAt()
                );
    }
}


