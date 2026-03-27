package com.example.paymentsystem.domain.payment.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class PaymentIdGenerator {

    /**
     * 포트원 결제 ID 생성 로직
     * 형식: payment_yyMMddHHmmss_userPart_random
     */
    public String generate(String userId) {
        // 1. 현재 시간 (yyMMddHHmmss)
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

        // 2. 유저 ID 추출 (최대 4글자, 특수문자 제거)
        String userPart = userId.replaceAll("[^a-zA-Z0-9]", "");
        if (userPart.length() > 4) {
            userPart = userPart.substring(0, 4);
        }

        // 3. 짧은 랜덤 문자열 (동시 요청 대비 - UUID의 앞 4자리 사용)
        String randomSuffix = UUID.randomUUID().toString().substring(0, 4);

        // 4. 최종 조합
        return String.format("payment_%s_%s_%s", timestamp, userPart, randomSuffix);
    }
}