package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
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
    private final RestTemplate restTemplate;
    private static final String PORTONE_URL = "https://api.portone.io/payments/";

    //공통헤더
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "PortOne " + portOneProperties.getApi().getSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);//바디 JSON형식 지정
        return headers;
    }

    //공통에러
    private ServiceException handlePortOneError(HttpClientErrorException e) {
        return switch (e.getStatusCode().value()) {
            case 401 -> new ServiceException(ErrorCode.PORTONE_AUTHENTICATION_FAILED);
            case 403 -> new ServiceException(ErrorCode.PORTONE_FORBIDDEN);
            case 404 -> new ServiceException(ErrorCode.PAYMENT_NOT_FOUND);
            case 409 -> new ServiceException(ErrorCode.ALREADY_CANCELLED_PAYMENT);
            default -> new ServiceException(
                    ErrorCode.PORTONE_API_ERROR,
                    "포트원 API 오류: " + e.getStatusCode());
        };
    }

    //결제 검증
    public PortOneVerificationResponseDto getVerifyPayment(String paymentId) {
        try {
            String portOneUrl = PORTONE_URL + paymentId;

            HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

            return restTemplate.exchange(
                    portOneUrl,
                    HttpMethod.GET,
                    entity,
                    PortOneVerificationResponseDto.class
            ).getBody();

        } catch (HttpClientErrorException e) {
            log.error("포트원 API 오류: {}", e.getStatusCode());
            throw handlePortOneError(e);//공통에러 메서드 사용
        } catch (Exception e) {
            log.error("포트원 서버 연결 오류: {}", e.getMessage());
            throw new RuntimeException("포트원 서버 연결에 실패했습니다.");
        }
    }

    //결제 취소
    public CancelResponseDto cancelPayment(String paymentId, CancelRequestDto request) {
        try {
            String portOneUrl = PORTONE_URL + paymentId + "/cancel";

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
            throw handlePortOneError(e);
        } catch (Exception e) {
            log.error("포트원 서버 연결 오류: {}", e.getMessage());
            throw new ServiceException(ErrorCode.PORTONE_SERVER_CONNECTION_FAILED);
        }
    }
}