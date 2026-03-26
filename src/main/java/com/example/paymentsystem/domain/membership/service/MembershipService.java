package com.example.paymentsystem.domain.membership.service;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.membership.dto.GetMembershipTierResponse;
import com.example.paymentsystem.domain.membership.dto.GetMyMembershipResponse;
import com.example.paymentsystem.domain.membership.entity.MembershipTier;
import com.example.paymentsystem.domain.membership.entity.UserMembership;
import com.example.paymentsystem.domain.membership.repository.MembershipTierRepository;
import com.example.paymentsystem.domain.membership.repository.UserMembershipRepository;
import com.example.paymentsystem.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {

    private final MembershipTierRepository membershipTierRepository;
    private final UserRepository userRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final OrderRepository orderRepository;

    // 멤버십 등급 정책 조회 (MembershipTier)
    public List<GetMembershipTierResponse> getMembershipTier() {
        return membershipTierRepository.findAll().stream()
                .map(GetMembershipTierResponse::new)
                .toList();
    }

    // 유저의 멤버십 등급 조회 (UserMembership)
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
    public void updateMembership(Long userId, Long totalPaidPrice) {
        // 1. 유저의 멤버십 조회
        UserMembership userMembership = userMembershipRepository.findByUserId(userId).orElseThrow(
                () -> new ServiceException(ErrorCode.MEMBERSHIP_NOT_FOUND)
        );
        // 2. 해당 유저의 모든 '주문 완료' 주문 금액 합계를 DB에서 직접 조회
        List<MembershipTier> allTiers = membershipTierRepository.findAll();

        // 반영
        userMembership.updateTotalPriceAndGrade(totalPaidPrice, allTiers);
    }
}
