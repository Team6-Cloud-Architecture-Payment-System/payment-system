package com.example.paymentsystem.domain.refund.repository;

import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.refund.entity.Refund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    // 특정 결제의 환불 조회용
    Optional<Refund> findByPaymentOrderId(Long orderId);

    // 이미 환불된 건지 체크용
    boolean existsByPayment(Payment payment);

    // 유저의 전체 환불내역 체크
    List<Refund> findAllByPaymentOrderUser(User user);

    Page<Refund> findAllByPaymentOrderUser(User user, Pageable pageable);
}
