package com.example.paymentsystem.domain.payment.entity;

import com.bootcamp.paymentdemo.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

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
    private String payment_status;
    private Long payment_price;
    private LocalDateTime payment_created_at;
    private LocalDateTime refund_created_at;

    public Payment(Long orderId, Long payments_id, String payment_status, Long payment_price, LocalDateTime payment_created_at, LocalDateTime refund_created_at) {
        this.orderId = orderId;
        this.payments_id = payments_id;
        this.payment_status = payment_status;
        this.payment_price = payment_price;
        this.payment_created_at = payment_created_at;
        this.refund_created_at = refund_created_at;
    }
}
