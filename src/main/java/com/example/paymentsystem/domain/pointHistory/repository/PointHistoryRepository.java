package com.example.paymentsystem.domain.pointHistory.repository;

import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.pointHistory.entity.PointHistory;
import com.example.paymentsystem.domain.pointHistory.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Optional<PointHistory> findByOrderAndType(Order order, Type type);

    // 포인트 합산 로직 추가
    @Query("SELECT SUM(p.point) FROM PointHistory p WHERE p.user = :user")
    Long sumPointByUser(@Param("user") User user);

    Page<PointHistory> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    boolean existsByOrderAndType(Order order, Type type);
}
