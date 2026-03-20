package com.example.paymentsystem.domain.order.repository;

import com.example.paymentsystem.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    Optional<Order> findByOrderNumber(UUID orderNumber);

   // Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
