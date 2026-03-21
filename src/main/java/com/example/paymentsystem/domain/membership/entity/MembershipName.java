package com.example.paymentsystem.domain.membership.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MembershipName {

    NORMAL("일반 등급"),
    VIP("우수 등급"),
    VVIP("최우수 등급");

    private final String name;
}
