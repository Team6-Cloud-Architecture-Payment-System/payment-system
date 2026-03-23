package com.example.paymentsystem.domain.membership.repository;

import com.example.paymentsystem.domain.membership.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    @Query("SELECT mt.rewardRate FROM UserMembership um " +
            "JOIN MembershipTier mt ON um.gradeNow = mt.membershipName " +
            "WHERE um.user.id = :userId")
    Optional<Double> findRewardRateByUserId(@Param("userId") Long userId);
}
