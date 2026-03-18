package com.example.paymentsystem.domain.payment.entity;

import com.example.paymentsystem.common.entity.BaseEntity;
import com.example.paymentsystem.domain.payment.status.PaymentStatus;
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
    private Long orderId;
    private Long payments_id;
    //Enum 클래스 생성 전 임시 status
    private PaymentStatus payment_status;
    private Long payment_price;
    private LocalDateTime createdAt;
    private LocalDateTime refund_created_at;

    public Payment(Long orderId, Long payments_id, PaymentStatus payment_status, Long payment_price) {
        this.orderId = orderId;
        this.payments_id = payments_id;
        this.payment_status = payment_status;
        this.payment_price = payment_price;
    }

    public void stateUpdate(PaymentStatus payment_status){
        this.payment_status = payment_status;
    }
}
