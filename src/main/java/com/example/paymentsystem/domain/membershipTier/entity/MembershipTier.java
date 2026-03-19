package com.example.paymentsystem.domain.membershipTier.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "membership_tiers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MembershipTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double rewardRate;

    // 승급 기준
    @Column(nullable = false)
    private String promotionCriteria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipName membershipName;

    @Column(nullable = false)
    private Long minPrice;

    private Long maxPrice;

    public MembershipTier(Double rewardRate, String promotionCriteria, MembershipName membershipName, Long minPrice, Long maxPrice) {
        this.rewardRate = rewardRate;
        this.promotionCriteria = promotionCriteria;
        this.membershipName = membershipName;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }


}
