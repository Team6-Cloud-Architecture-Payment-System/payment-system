package com.example.paymentsystem.domain.membership.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MembershipController {

    private final MembershipService membershipService;

    // 멤버십 등급 정책 조회
    @GetMapping("/membership/tiers")
    public ResponseEntity<ApiResponse> getMembershipTiers() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(membershipService.getMembershipTier()));
    }

    // 유저의 멤버십 등급 조회
    @GetMapping("/membership/me")
    public ResponseEntity<ApiResponse> getMyMembership(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(membershipService.getMyMembership(userId)));
    }
}
