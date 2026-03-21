package com.example.paymentsystem.domain.membership.service;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.membership.dto.GetMembershipTierResponse;
import com.example.paymentsystem.domain.membership.dto.GetMyMembershipResponse;
import com.example.paymentsystem.domain.membership.entity.UserMembership;
import com.example.paymentsystem.domain.membership.repository.MembershipTierRepository;
import com.example.paymentsystem.domain.membership.repository.UserMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipTierRepository membershipTierRepository;
    private final UserRepository userRepository;
    private final UserMembershipRepository userMembershipRepository;

    // 멤버십 등급 정책 조회 (MembershipTier)
    @Transactional(readOnly = true)
    public List<GetMembershipTierResponse> getMembershipTier() {
        return membershipTierRepository.findAll().stream()
                .map(GetMembershipTierResponse::new)
                .toList();
    }

    // 유저의 멤버십 등급 조회 (UserMembership)
    @Transactional(readOnly = true)
    public GetMyMembershipResponse getMyMembership(Long userId) {
        // 1. 유저 조회
        userRepository.findById(userId).orElseThrow(
                () -> new ServiceException(ErrorCode.USER_NOT_FOUND)
        );
        // 2. 유저의 멤버십 정보 조회
        UserMembership userMembership = userMembershipRepository.findByUserId(userId).orElseThrow(
                () -> new ServiceException(ErrorCode.MEMBERSHIP_NOT_FOUND)
        );

        return new GetMyMembershipResponse(userMembership);
    }

    // 등급 갱신
    @Transactional
    public void updateMembership(User user, Long totalPaidPrice) {
        // 1. 유저의 멤버십 조회
        UserMembership userMembership = userMembershipRepository.findByUserId(user.getId()).orElseThrow(
                () -> new ServiceException(ErrorCode.MEMBERSHIP_NOT_FOUND)
        );
        // 엔티티로 등급 계산 넘김
        userMembership.updateTotalPriceAndGrade(totalPaidPrice);
        userMembershipRepository.save(userMembership);
    }
}
