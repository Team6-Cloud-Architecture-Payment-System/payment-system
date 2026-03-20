package com.example.paymentsystem.domain.order.dto;

import com.example.paymentsystem.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderResponse(

        Long orderId,
        UUID orderNumber,
        Long totalPrice,
        Long usedPoint,
        Long paymentPrice,
        String orderStatus,
        LocalDateTime orderedCreatedAt
) {
    public CreateOrderResponse(Order order) {
        this(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getUsedPoint(),
                order.getPaymentPrice(),
                order.getOrderStatus().getStatusName(),
                order.getCreatedAt()
        );
    }
}
