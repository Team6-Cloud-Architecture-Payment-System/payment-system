package com.example.paymentsystem.domain.order.repository;

import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    @EntityGraph(attributePaths = "user")
    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findAllByOrderStatus(OrderStatus orderStatus);
}
