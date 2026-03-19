package com.example.paymentsystem.domain.refund.repository;

import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    // 특정 결제의 환불 조회용
    Optional<Refund> findByPaymentOrderId(Long orderId);

    // 이미 환불된 건지 체크용
    boolean existsByPayment(Payment payment);
}
