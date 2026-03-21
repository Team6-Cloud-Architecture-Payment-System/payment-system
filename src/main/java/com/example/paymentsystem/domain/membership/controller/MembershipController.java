package com.example.paymentsystem.domain.membership.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.membership.dto.GetMembershipTierResponse;
import com.example.paymentsystem.domain.membership.dto.GetMyMembershipResponse;
import com.example.paymentsystem.domain.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MembershipController {

    private final MembershipService membershipService;

    // 멤버십 등급 정책 조회
    @GetMapping("/membership/tiers")
    public ResponseEntity<ApiResponse<List<GetMembershipTierResponse>>> getMembershipTiers() {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(membershipService.getMembershipTier()));
    }

    // 유저의 멤버십 등급 조회
    @GetMapping("/membership/me")
    public ResponseEntity<ApiResponse<GetMyMembershipResponse>> getMyMembership(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(membershipService.getMyMembership(userId)));
    }
}
