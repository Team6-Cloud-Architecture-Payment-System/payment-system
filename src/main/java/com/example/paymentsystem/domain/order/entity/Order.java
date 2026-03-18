package com.example.paymentsystem.domain.order.entity;

import com.example.paymentsystem.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private UUID orderNumber = UUID.randomUUID();

    @Column(nullable = false)
    private Integer totalAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public Order(Long userId, Integer totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.orderStatus = OrderStatus.PAYMENT_PENDING;
    }
}