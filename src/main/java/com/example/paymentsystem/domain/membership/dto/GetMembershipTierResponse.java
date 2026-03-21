package com.example.paymentsystem.domain.membership.dto;

import com.example.paymentsystem.domain.membership.entity.MembershipName;
import com.example.paymentsystem.domain.membership.entity.MembershipTier;

// 멤버십 등급 정책 DTO
public record GetMembershipTierResponse(
        Long id,
        Double rewardRate,
        String promotionCriteria,
        MembershipName membershipName,
        Long minPrice,
        Long maxPrice

) {
    public GetMembershipTierResponse(MembershipTier membershipTier) {
        this(
                membershipTier.getId(),
                membershipTier.getRewardRate(),
                membershipTier.getPromotionCriteria(),
                membershipTier.getMembershipName(),
                membershipTier.getMinPrice(),
                membershipTier.getMaxPrice()
        );
    }
}

