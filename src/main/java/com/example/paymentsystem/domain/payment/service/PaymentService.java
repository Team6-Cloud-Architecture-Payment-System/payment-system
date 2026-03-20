package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.repository.OrderRepository;
import com.example.paymentsystem.domain.payment.dto.PaymentTryRequest;
import com.example.paymentsystem.domain.payment.dto.PaymentTryResponse;
import com.example.paymentsystem.domain.payment.dto.WebhookRequestDto;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.Webhook;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.repository.WebhookRepository;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final WebhookRepository webhookRepository;
    private final OrderRepository orderRepository;
    private final PaymentIdGenerator paymentIdGenerator;

    public PaymentTryResponse tryPayment(Long orderId, PaymentTryRequest request) {

        //1. 존재 하는 주문인지

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException("주문을 찾을 수 없습니다.")
        );

        //2. 주문 금액이 결제 금액과 동일한지
        // 수정함!! 변경 확인 필요
        if (order.getTotalPrice() != request.payment_price().intValue()) {
            throw new IllegalStateException("주문 금액이 일치하지 않습니다.");
        }

        //3. PortOne에 보낼 payments_id 생성

        String generatedPaymentId = paymentIdGenerator.generate(String.valueOf(order.getUser().getId()));

        //4. 결제 테이블에 저장 (주문 id, payments_id, 결제 상태(대기 기본값), 결제 금액)

        return new PaymentTryResponse(paymentRepository.save(new Payment(
                order,
                generatedPaymentId,
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
