package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.common.properties.PortOneProperties;
import com.example.paymentsystem.domain.payment.dto.PortOneResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PortOneService {

    private final PortOneProperties portOneProperties;//키불러오기위해
    private final RestTemplate restTemplate = new RestTemplate();

    public PortOneResponseDto getPayment(String paymentId) {

        String portOneUrl = "https://api.portone.io/payments/"+paymentId;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","PortOne"+portOneProperties.getApi().getSecret());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return null;
    }
}