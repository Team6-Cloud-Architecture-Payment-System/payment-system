package com.example.paymentsystem.domain.order.dto;

import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.entity.OrderItem;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse (

        Long orderId,
        String orderNumber,
        Long totalPrice,
        Long usedPoint,
        Long paymentPrice,
        String orderStatus,
        LocalDateTime orderedCreatedAt,
        List<OrderItemDetail> orderItems
) {
    public OrderDetailResponse(Order order) {
        this(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getUsedPoint(),
                order.getPaymentPrice(),
                order.getOrderStatus().getStatusName(),
                order.getCreatedAt(),
                order.getOrderItems().stream()
                        .map(OrderItemDetail::new)
                        .toList()
        );
    }

    public record OrderItemDetail(
            Long productId,
            String productName,
            Long productPrice,
            Long quantity,
            Long subtotalPrice
    ) {
        public OrderItemDetail(OrderItem orderItem) {
            this(
                    orderItem.getProductId(),
                    orderItem.getProductName(),
                    orderItem.getProductPrice(),
                    orderItem.getQuantity(),
                    orderItem.getSubtotalPrice()
            );
        }
    }
}
