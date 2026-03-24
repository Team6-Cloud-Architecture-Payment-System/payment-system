package com.example.paymentsystem.domain.order.dto;

import com.example.paymentsystem.domain.order.entity.Order;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public record OrderHistoryResponse(

        List<OrderSummary> orders,
        Pagination pagination
) {
    public static OrderHistoryResponse from(Page<Order> orderPage) {
        List<OrderSummary> orders = orderPage.getContent().stream()
                .map(OrderSummary::new)
                .toList();

        // 페이지 정보를 Pagination DTO로 생성
        Pagination pagination = new Pagination(
                // 시작 페이지를 0이 아니라 1부터 보여줌
                orderPage.getNumber() + 1,
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
        // 주문 목록과 페이지 정보를 하나의 응답 DTO로 묶어서 반환
        return new OrderHistoryResponse(orders, pagination);
    }

    // 주문 목록에서 개별 주문 1건을 담음
    public record OrderSummary(
            Long orderId,
            String orderNumber,
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
