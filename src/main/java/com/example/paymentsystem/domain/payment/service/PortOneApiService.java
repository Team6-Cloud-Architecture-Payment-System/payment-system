package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.domain.payment.dto.PortOnePaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PortOneApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${portone.api.secret}")
    private String apiSecret;

    private static final String API_URL = "https://api.portone.io/payments/";

    /**
     * 포트원 결제 단건 조회 (최신 API 방식)
     */
    public PortOnePaymentResponse getPaymentData(String paymentId) {
        HttpHeaders headers = new HttpHeaders();
        // 인증 방식: "PortOne " 접두어와 Secret Key 조합
        headers.set("Authorization", "PortOne " + apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PortOnePaymentResponse> response = restTemplate.exchange(
                    API_URL + paymentId,
                    HttpMethod.GET,
                    entity,
                    PortOnePaymentResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("포트원 결제 정보 조회 실패: " + e.getMessage());
        }
    }
}