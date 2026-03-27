package com.example.paymentsystem.domain.auth.repository;


import com.example.paymentsystem.domain.auth.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 핵심: 비관적 쓰기 락
    @Query("select u from User u where u.id = :id")
    Optional<User> findByIdWithPessimisticLock(@Param("id") Long id);
}
