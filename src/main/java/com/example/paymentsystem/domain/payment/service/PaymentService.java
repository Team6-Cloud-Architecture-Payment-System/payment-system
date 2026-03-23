package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.entity.OrderStatus;
import com.example.paymentsystem.domain.order.repository.OrderRepository;
import com.example.paymentsystem.domain.order.service.OrderService;
import com.example.paymentsystem.domain.payment.dto.*;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.Webhook;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.repository.WebhookRepository;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final WebhookRepository webhookRepository;
    private final OrderRepository orderRepository;
    private final PaymentIdGenerator paymentIdGenerator;
    private final PortOneService portApiService;
    private final OrderService orderService;

    @Transactional
    public PaymentTryResponse tryPayment(Long orderId, PaymentTryRequest request) {

        //1. 존재 하는 주문인지

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException("주문을 찾을 수 없습니다.")
        );

        //2. 주문 금액이 결제 금액과 동일한지
        if (order.getTotalPrice().equals(request.paymentPrice())) {
            throw new IllegalStateException("주문 금액이 일치하지 않습니다.");
        }

        if (!order.getOrderStatus().equals(OrderStatus.PAYMENT_PENDING)) {
            throw new IllegalStateException("결제할 수 없는 주문 상태입니다.");
        }

        if (paymentRepository.existsByOrderAndPaymentStatus(order, PaymentStatus.WAIT)) {
            throw new IllegalStateException("이미 결제 진행 중인 주문입니다.");
        }

        //3. PortOne에 보낼 payments_id 생성

        String generatedPaymentId = paymentIdGenerator.generate(String.valueOf(order.getUser().getId()));

        //4. 결제 테이블에 저장 (주문 id, payments_id, 결제 상태(대기 기본값), 결제 금액)

        Payment saved = new Payment(
                order,
                generatedPaymentId,
                PaymentStatus.WAIT,
                request.paymentPrice());

        return new PaymentTryResponse(paymentRepository.save(saved));
    }

    @Transactional
    public void confirmPayment(String paymentId) {
        // 1. 포트원 서버에서 실제 결제 내역 조회
        PortOneVerificationResponseDto portOneData = portApiService.getVerifyPayment(paymentId);

        // 2. 우리 DB에서 결제 시도 정보 조회
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));

        // 3. 중복 처리 방지 (이미 완료된 경우 종료)
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            return;
        }

        // 4. 결제 상태 확인 (PAID 인지 확인)
        if (!portOneData.getStatus().equals(PaymentStatus.PAID.toString())) {
            throw new IllegalStateException("결제가 완료되지 않은 상태입니다. 상태: " + portOneData.getStatus());
        }

        // 5. 금액 검증 (DB 저장 금액 vs 포트원 실제 결제 금액)
        if (portOneData.getAmount().getTotal() != payment.getPaymentPrice()) {
            // 위변조 시 즉시 예외 발생 (필요 시 자동 환불 로직 추가)
            throw new IllegalStateException("결제 금액 위변조가 감지되었습니다.");
        }

        // 6. 상태 업데이트 및 비즈니스 로직 수행
        payment.stateUpdate(PaymentStatus.PAID);
        orderService.confirmOrder(
                payment.getOrder().getId(),
                payment.getOrder().getUser().getId());
    }

    private void handleSecurityIssue(String impUid, String token) {
        // 환불 API 호출
        log.error("err: 금액 불일치 imp_uid: {}", impUid);
    }

    @Transactional
    public void receiveWebhook(WebhookRequestDto dto,String webhookId) {

        //webhook_id 중복 체크
        if(webhookRepository.existsByWebhookId(webhookId)){
            return;
        }

        //        중복이면 → 그냥 리턴
//        중복 아니면 → 웹훅 테이블에 RECEIVED로 저장
//        paymentId로 PortOne 결제 조회
//        결제 확정 처리
//        웹훅 테이블 상태 → PROCESSED 업데이트

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
