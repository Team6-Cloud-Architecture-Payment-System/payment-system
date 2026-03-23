package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.common.properties.PortOneProperties;
import com.example.paymentsystem.domain.payment.dto.CancelRequestDto;
import com.example.paymentsystem.domain.payment.dto.CancelResponseDto;
import com.example.paymentsystem.domain.payment.dto.PortOneVerificationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortOneService {

    private final PortOneProperties portOneProperties;//키불러오기위해
    private final RestTemplate restTemplate = new RestTemplate();

    //공통헤더
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "PortOne " + portOneProperties.getApi().getSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);//바디 JSON형식 지정
        return headers;
    }

    //결제 검증
    public PortOneVerificationResponseDto getVerifyPayment(String paymentId) {
        try {
            String portOneUrl = "https://api.portone.io/payments/" + paymentId;

            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

            return restTemplate.exchange(
                    portOneUrl,
                    HttpMethod.GET,
                    entity,
                    PortOneVerificationResponseDto.class
            ).getBody();

        } catch (HttpClientErrorException e) {
            log.error("포트원 API 오류: {}", e.getStatusCode());
            switch (e.getStatusCode().value()) {
                case 401, 403 -> throw new RuntimeException("포트원 인증 실패: 시크릿 키를 확인해주세요.");
                case 404 -> throw new RuntimeException("존재하지 않는 paymentId입니다.");
                default -> throw new RuntimeException("포트원 API 오류: " + e.getStatusCode());
            }
        } catch (Exception e) {
            log.error("포트원 서버 연결 오류: {}", e.getMessage());
            throw new RuntimeException("포트원 서버 연결에 실패했습니다.");
        }
    }

    //결제 취소
    public CancelResponseDto cancelPayment(String paymentId, CancelRequestDto request) {
        try {
            String portOneUrl = "https://api.portone.io/payments/" + paymentId + "/cancel";

            Map<String, Object> body = new HashMap<>();
            if (StringUtils.hasText(request.reason())) {
                body.put("reason", request.reason());
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, createHeaders());

            return restTemplate.exchange(
                    portOneUrl,
                    HttpMethod.POST,
                    entity,
                    CancelResponseDto.class
            ).getBody();

        } catch (HttpClientErrorException e) {
            log.error("포트원 취소 오류: {}", e.getStatusCode());
            switch (e.getStatusCode().value()) {
                case 401, 403 -> throw new RuntimeException("포트원 인증 실패");
                case 404 -> throw new RuntimeException("존재하지 않는 paymentId입니다.");
                case 409 -> throw new RuntimeException("이미 취소된 결제입니다.");
                default -> throw new RuntimeException("포트원 취소 API 오류: " + e.getStatusCode());
            }
        } catch (Exception e) {
            log.error("포트원 서버 연결 오류: {}", e.getMessage());
            throw new RuntimeException("포트원 서버 연결에 실패했습니다.");
        }
    }
}