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
    private final PortOneApiService portOneApiService;
    private final OrderService orderService;

    @Transactional
    public PaymentTryResponse tryPayment(Long orderId, PaymentTryRequest request) {

        //1. 존재 하는 주문인지

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalStateException("주문을 찾을 수 없습니다.")
        );

        //2. 주문 금액이 결제 금액과 동일한지
        if (order.getTotalPrice().equals(request.payment_price())) {
            throw new IllegalStateException("주문 금액이 일치하지 않습니다.");
        }

        if (!order.getOrderStatus().equals(OrderStatus.PAYMENT_PENDING)) {
            throw new IllegalStateException("결제할 수 없는 주문 상태입니다.");
        }

        if (paymentRepository.existsByOrderAndStatus(order, PaymentStatus.WAIT)) {
            throw new IllegalStateException("이미 결제 진행 중인 주문입니다.");
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
    public String confirmPayment(PaymentConfirmRequest request) {
        // 1. 포트원 액세스 토큰 발급
        String accessToken = portOneApiService.getAccessToken();

        // 2. 포트원 서버에서 실제 결제 내역 조회
        PortOnePaymentResponse.PaymentDetail portOneData =
                portOneApiService.getPaymentData(request.imp_uid(), accessToken);

        // 3. DB에서 결제 시도 정보 조회
        Payment payment = paymentRepository.findByPaymentsId(request.paymentsId())
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));

        // 4. 이미 처리된 결제인지 확인
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            return accessToken;
        }

        // 5. 금액 검증: DB의 요청 금액과 실제 결제된 금액이 일치하는지 확인
        if (!portOneData.amount().equals(payment.getPaymentPrice())) {
            handleSecurityIssue(request.imp_uid(), accessToken);
            throw new IllegalStateException("결제 금액 위변조가 감지되었습니다.");
        }

        // 6. 결제 확정 처리
        payment.stateUpdate(PaymentStatus.PAID);

        // 7. 후속 비즈니스 로직 (재고 차감, 포인트 적립 등)
//        orderService.(payment.getOrder());
        return accessToken;
    }

    private void handleSecurityIssue(String impUid, String token) {
        // 환불 API 호출
        log.error("err: 금액 불일치 imp_uid: {}", impUid);
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
