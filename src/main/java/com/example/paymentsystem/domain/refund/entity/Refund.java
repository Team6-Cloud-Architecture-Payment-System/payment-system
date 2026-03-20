package com.example.paymentsystem.domain.refund.entity;

import com.example.paymentsystem.domain.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "refunds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Refund {
    // BaseEntity를 받아 사용할지 안 할지 결정

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private Long refundPrice;

    @Column(nullable = false)
    private String refundReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus status;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;


    public Refund(Payment payment, Long refundPrice, String refundReason, RefundStatus status) {
        this.payment = payment;
        this.refundPrice = refundPrice;
        this.refundReason = refundReason;
        this.status = status;
    }

    public void updateRefund(RefundStatus status) {
        this.status = status;
    }

}
