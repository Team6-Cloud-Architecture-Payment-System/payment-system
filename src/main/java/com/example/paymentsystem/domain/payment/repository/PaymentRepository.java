package com.example.paymentsystem.domain.payment.repository;


import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentsId);

    boolean existsByOrderAndPaymentStatus(Order order, PaymentStatus paymentStatus);
}
