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
    private String paymentsId;
    //Enum 클래스 생성 전 임시 status
    private PaymentStatus paymentStatus;
    private Long paymentPrice;
    private LocalDateTime createdAt;
    private LocalDateTime refund_created_at;

    public Payment(Order order, String paymentId, PaymentStatus paymentStatus, Long paymentPrice) {
        this.order = order;
        this.paymentsId = paymentId;
        this.paymentStatus = paymentStatus;
        this.paymentPrice = paymentPrice;
    }

    public void stateUpdate(PaymentStatus payment_status){
        this.paymentStatus = payment_status;
    }
}
