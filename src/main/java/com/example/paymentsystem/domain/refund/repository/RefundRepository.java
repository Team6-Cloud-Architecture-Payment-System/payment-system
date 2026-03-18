package com.example.paymentsystem.domain.refund.repository;

import com.example.paymentsystem.domain.refund.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByPaymentOrderId(Long orderId);
}
