package com.example.paymentsystem.domain.payment.repository;


import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentsId);

    boolean existsByOrderAndPaymentStatus(Order order, PaymentStatus paymentStatus);

    List<Payment> findByOrderId(Long orderId);

    List<Payment> findByOrderAndPaymentStatus(Order order, PaymentStatus paymentStatus);

    //비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.paymentId = :paymentId")
    Optional<Payment> findByPaymentIdWithLock(@Param("paymentId") String paymentId);

}
