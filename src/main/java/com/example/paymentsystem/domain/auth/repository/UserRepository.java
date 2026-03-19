package com.example.paymentsystem.domain.auth.repository;

import com.example.paymentsystem.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
