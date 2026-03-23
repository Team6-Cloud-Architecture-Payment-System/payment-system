package com.example.paymentsystem.domain.order.dto;

import com.example.paymentsystem.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateOrderResponse(

        Long orderId,
        String orderNumber,
        Long totalPrice,
        Long usedPoint,
        Long paymentPrice,
        String orderStatus,
        LocalDateTime orderedCreatedAt
)  {
    public static CreateOrderResponse from(Order order) {
        return new CreateOrderResponse(
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
