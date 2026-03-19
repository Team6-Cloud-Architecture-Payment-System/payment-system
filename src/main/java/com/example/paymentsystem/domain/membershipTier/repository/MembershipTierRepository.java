package com.example.paymentsystem.domain.membershipTier.repository;

import com.example.paymentsystem.domain.membershipTier.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
}
