package com.example.paymentsystem.domain.order.dto;

import com.example.paymentsystem.domain.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderHistoryResponse(

        List<OrderSummary> orders,
        Pagination pagination
) {
    // 주문 목록에서 개별 주문 1건을 담음
    public record OrderSummary(
            Long orderId,
            UUID orderNumber,
            Long totalPrice,
            Long usedPoint,
            Long paymentPrice,
            String orderStatus,
            LocalDateTime orderedCreatedAt
    ) {

        public OrderSummary(Order order) {
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

    // 페이징 정보
    public record Pagination(
            // 현재 페이지
            int currentPage,
            //페이지당 데이터 수
            int size,
            // 전체 데이터
            long totalCount,
            // 전체 페이지
            int totalPages
    ) {
    }
}
