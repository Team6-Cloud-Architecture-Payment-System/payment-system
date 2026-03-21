package com.example.paymentsystem.domain.membership.repository;

import com.example.paymentsystem.domain.membership.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
}
