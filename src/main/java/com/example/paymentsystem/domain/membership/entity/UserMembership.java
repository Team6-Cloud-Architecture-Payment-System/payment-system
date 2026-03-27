package com.example.paymentsystem.domain.membership.entity;

import com.example.paymentsystem.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Entity
@Table(name = "user_memberships")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MembershipName gradeNow;

    @Column(nullable = false)
    private Long totalPrice;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime gradeUpdatedAt;

    // 처음 생성시 기본등급, 총결제금 0원으로
    public UserMembership(User user) {
        this.user = user;
        this.gradeNow = MembershipName.NORMAL;
        this.totalPrice = 0L;
    }

    // 등급 계산 로직
    public void updateTotalPriceAndGrade(Long addedPrice, List<MembershipTier> tiers) {
        // 1. 누적 금액 업데이트
        this.totalPrice += addedPrice;

        // 2. 등급 재계산 (DB 정책 기반)
        // minPrice가 높은 순서대로 정렬하여 가장 먼저 만족하는 등급을 선택
        this.gradeNow = tiers.stream()
                .filter(tier -> this.totalPrice >= tier.getMinPrice())
                .sorted(Comparator.comparing(MembershipTier::getMinPrice).reversed())
                .map(MembershipTier::getMembershipName)
                .findFirst()
                .orElse(MembershipName.NORMAL); // 기본값

        this.gradeUpdatedAt = LocalDateTime.now();
    }
}
