package com.example.paymentsystem.domain.membershipTier.service;

import com.example.paymentsystem.domain.membershipTier.repository.MembershipTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipTierService {

    private final MembershipTierRepository membershipTierRepository;
}
