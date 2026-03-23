package com.example.paymentsystem.domain.membership.dto;

import com.example.paymentsystem.domain.membership.entity.MembershipName;
import com.example.paymentsystem.domain.membership.entity.UserMembership;

import java.time.LocalDateTime;

// 유저의 멤버십 DTO
public record GetMyMembershipResponse (
        Long id,
        Long userId,
        MembershipName gradeNow,
        Long totalPrice,
        LocalDateTime gradeUpdatedAt
){
    public GetMyMembershipResponse(UserMembership userMembership){
        this(
                userMembership.getId(),
                userMembership.getUser().getId(),
                userMembership.getGradeNow(),
                userMembership.getTotalPrice(),
                userMembership.getGradeUpdatedAt()
        );
    }
}
