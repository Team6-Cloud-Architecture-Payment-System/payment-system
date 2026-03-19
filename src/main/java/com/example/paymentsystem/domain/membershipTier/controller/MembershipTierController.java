package com.example.paymentsystem.domain.membershipTier.controller;

import com.example.paymentsystem.domain.membershipTier.service.MembershipTierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MembershipTierController {

    private final MembershipTierService membershipTierService;
}
