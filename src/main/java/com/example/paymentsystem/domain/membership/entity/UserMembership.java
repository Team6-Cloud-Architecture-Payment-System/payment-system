package com.example.paymentsystem.domain.membership.entity;

import com.example.paymentsystem.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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
    private MembershipName recalculatingGrade(Long totalPrice) {
        if (totalPrice <= 50000) {
            return MembershipName.NORMAL;
        } else if (totalPrice <= 100000){
            return MembershipName.VIP;
        } else {
            return MembershipName.VVIP;
        }
    }

    // 총 결제 금액 업데이트 및 등급 재 계산
    public void updateTotalPriceAndGrade(Long totalPrice) {
        this.totalPrice += totalPrice;
        this.gradeNow = recalculatingGrade(this.totalPrice);
    }
}
