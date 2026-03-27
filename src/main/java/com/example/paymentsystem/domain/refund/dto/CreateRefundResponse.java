package com.example.paymentsystem.domain.refund.dto;

import com.example.paymentsystem.domain.refund.entity.Refund;
import com.example.paymentsystem.domain.refund.entity.RefundStatus;

import java.time.LocalDateTime;

public record CreateRefundResponse(

        Long id,
        Long paymentId,
        Long refundPrice,
        String refundReason,
        RefundStatus status,
        LocalDateTime createdAt

) {
    public CreateRefundResponse(Refund refund) {
        this(refund.getId(), refund.getPayment().getId(), refund.getRefundPrice(),
                refund.getRefundReason(), refund.getStatus(), refund.getCreatedAt());
    }
}
