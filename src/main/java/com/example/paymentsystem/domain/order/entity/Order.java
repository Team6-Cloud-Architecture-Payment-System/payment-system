package com.example.paymentsystem.domain.order.entity;

import com.example.paymentsystem.common.entity.BaseEntity;
import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String orderNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 상품 총액 (포인트 반영 전)
    @Column(nullable = false)
    private Long totalPrice;

    // 사용 요청한 포인트
    private Long usedPoint;

    // 결제 금액 (포인트 반영 후)
    @Column(nullable = false)
    private Long paymentPrice;

    // 주문 상태
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column
    private LocalDateTime orderCompletedAt;


    public Order(User user, Long totalPrice, Long usedPoint, Long paymentPrice) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.usedPoint = usedPoint;
        this.paymentPrice = paymentPrice;
        this.orderNumber = generateOrderNumber();
        this.orderStatus = OrderStatus.PAYMENT_PENDING;
    }

    // 주문 번호 커스텀
    private String generateOrderNumber() {
        return "ORD-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }

    // OrderItem 하나를 받아서 현재 Order가 가진 orderItems 리스트에 추가한다
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.assignOrder(this);
    }

    // 주문 완료
    public void complete() {
        if (this.orderStatus != OrderStatus.PAYMENT_PENDING) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }
        this.orderStatus = OrderStatus.ORDER_COMPLETED;
        this.orderCompletedAt = LocalDateTime.now();
    }

    // 주문 확정
    public void confirm() {
        if (this.orderStatus != OrderStatus.ORDER_COMPLETED) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }
        this.orderStatus = OrderStatus.ORDER_CONFIRMED;
    }

    // 주문 자동 확정 대상인지 확인 (주문 완료 후 5일 지났는지)
    public boolean canAutoConfirm(LocalDateTime now) {
        return this.orderStatus == OrderStatus.ORDER_COMPLETED
                && this.orderCompletedAt != null
                && !this.orderCompletedAt.plusDays(5).isAfter(now);
    }
}