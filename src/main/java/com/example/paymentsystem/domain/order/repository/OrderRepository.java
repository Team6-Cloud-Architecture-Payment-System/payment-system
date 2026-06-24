package com.example.paymentsystem.domain.order.repository;

import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.entity.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :orderId and o.user.id = :userId")
    Optional<Order> findByIdAndUserIdWithPessimisticLock(@Param("orderId") Long orderId, @Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdWithPessimisticLock(@Param("id") Long id);

    @EntityGraph(attributePaths = "user")
    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findAllByOrderStatus(OrderStatus orderStatus);

    @Query("""
                SELECT o
                FROM Order o
                WHERE o.orderStatus = com.example.paymentsystem.domain.order.entity.OrderStatus.ORDER_COMPLETED
                  AND o.orderCompletedAt <= :threshold
            """)
    List<Order> findOrdersReadyForAutoConfirm(@Param("threshold") LocalDateTime threshold);
}
