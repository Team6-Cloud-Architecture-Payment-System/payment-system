package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.domain.payment.dto.PortOnePaymentResponse;
import com.example.paymentsystem.domain.payment.dto.PortOneTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PortOneApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;

    // 1. 포트원 액세스 토큰 발급
    public String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";

        Map<String, String> body = new HashMap<>();
        body.put("imp_key", apiKey);
        body.put("imp_secret", apiSecret);

        PortOneTokenResponse response = restTemplate.postForObject(url, body, PortOneTokenResponse.class);

        if (response == null || response.response() == null) {
            throw new RuntimeException("포트원 토큰 발급에 실패했습니다.");
        }

        return response.response().access_token();
    }

    public PortOnePaymentResponse.PaymentDetail getPaymentData(String impUid, String accessToken) {
        String url = "https://api.iamport.kr/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PortOnePaymentResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, PortOnePaymentResponse.class
        );

        if (response.getBody() == null || response.getBody().response() == null) {
            throw new RuntimeException("결제 정보 조회에 실패했습니다.");
        }

        return response.getBody().response();
    }
}
