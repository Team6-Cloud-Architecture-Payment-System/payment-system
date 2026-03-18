package com.example.paymentsystem.domain.refund.entity;

import com.example.paymentsystem.domain.payment.entity.Payment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "refunds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {
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

    @Column(nullable = false)
    private RefundStatus status;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime refundCreatedAt;

    public Refund(Payment payment, Long refundPrice, String refundReason, RefundStatus status,  LocalDateTime refundCreatedAt) {
        this.payment = payment;
        this.refundPrice = refundPrice;
        this.refundReason = refundReason;
        this.status = status;
        this.refundCreatedAt = refundCreatedAt;
    }

}
