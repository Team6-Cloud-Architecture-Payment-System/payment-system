package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.domain.payment.dto.PaymentTryRequest;
import com.example.paymentsystem.domain.payment.dto.PaymentTryResponse;
import com.example.paymentsystem.domain.payment.dto.WebhookRequestDto;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.Webhook;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.repository.WebhookRepository;
import com.example.paymentsystem.domain.payment.status.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final WebhookRepository webhookRepository;

    public PaymentTryResponse tryPayment(Long orderId, PaymentTryRequest request) {

//        Order order = orderRepository.findById(orderId);

        return new PaymentTryResponse(paymentRepository.save(new Payment(
                orderId,
                request.payments_id(),
                PaymentStatus.WAIT,
                request.payment_price())));
    }

    @Transactional
    public void receiveWebhook(WebhookRequestDto dto,String webhookId) {

        //이미 처리된 paymentId인 경우
        if(webhookRepository.existsByPaymentId(dto.data().paymentId())){
            return;
        }

        // 정적 팩토리 메서드로 객체 생성
        Webhook webhook = Webhook.of(dto,webhookId);

        // 저장
        webhookRepository.save(webhook);
    }
}
