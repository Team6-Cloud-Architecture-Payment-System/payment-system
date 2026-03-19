package com.example.paymentsystem.domain.payment.entity;

import com.example.paymentsystem.common.entity.BaseEntity;
import com.example.paymentsystem.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "payments")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //주문 엔티티 생성 전 임시 id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    private String payments_id;
    //Enum 클래스 생성 전 임시 status
    private PaymentStatus payment_status;
    private Long payment_price;
    private LocalDateTime createdAt;
    private LocalDateTime refund_created_at;

    public Payment(Order order, String payments_id, PaymentStatus payment_status, Long payment_price) {
        this.order = order;
        this.payments_id = payments_id;
        this.payment_status = payment_status;
        this.payment_price = payment_price;
    }

    public void stateUpdate(PaymentStatus payment_status){
        this.payment_status = payment_status;
    }
}
