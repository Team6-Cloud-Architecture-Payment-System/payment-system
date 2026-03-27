package com.example.paymentsystem.domain.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(

        @NotEmpty(message = "주문 상품은 1개 이상이어야 합니다.")
        List<@Valid CreateOrderItemRequest> orderItems,

        @Min(value = 0, message = "사용 포인트는 0 이상이어야 합니다.")
        Long usedPoint

) {
    // usedPoint가 null이면 0으로 변경
    public CreateOrderRequest {
        if (usedPoint == null) {
            usedPoint = 0L;
        }
    }
}
