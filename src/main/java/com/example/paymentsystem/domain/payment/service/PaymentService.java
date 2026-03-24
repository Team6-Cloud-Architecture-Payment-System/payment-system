package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.entity.OrderStatus;
import com.example.paymentsystem.domain.order.repository.OrderRepository;
import com.example.paymentsystem.domain.order.service.OrderService;
import com.example.paymentsystem.domain.payment.dto.*;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.repository.WebhookRepository;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public PaymentTryResponse tryPayment(Long orderId) {

        // 1. 존재하는 주문인지 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));

        // 2. 결제 가능한 주문 상태인지 검증
        if (!order.getOrderStatus().equals(OrderStatus.PAYMENT_PENDING)) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // 3. 기존 PENDING 상태의 결제가 존재하면 FAIL 처리
        if (paymentRepository.existsByOrderAndPaymentStatus(order, PaymentStatus.PENDING)) {
            List<Payment> payments = paymentRepository.findByOrderId(orderId);
            for (Payment payment : payments) {
                payment.stateUpdate(PaymentStatus.FAIL);
            }
        }

        // 4. PortOne에 보낼 paymentId 생성
        String generatedPaymentId = paymentIdGenerator.generate(
                String.valueOf(order.getUser().getId())
        );

        // 5. 결제 테이블에 저장 (주문 id, paymentId, 결제 상태(PENDING), 결제 금액)
        Payment saved = new Payment(
                order,
                generatedPaymentId,
                PaymentStatus.PENDING,
                order.getPaymentPrice()
        );

        return new PaymentTryResponse(paymentRepository.save(saved));
    }

    @Transactional
    public void confirmPayment(String paymentId) {

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ServiceException(ErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getPaymentPrice() == 0) {
            orderService.stockReduce(payment.getOrder());
            return;
        }

        PortOneVerificationResponseDto portOneData = portApiService.getVerifyPayment(paymentId);

        // 3. 중복 처리 방지 (이미 완료된 경우 예외)
        if (payment.getPaymentStatus() == PaymentStatus.PAID) {
            return;
        }

        // 4. 포트원 결제 상태 확인 (PAID 여부)
        if (!portOneData.getStatus().equals(PaymentStatus.PAID.toString())) {
            throw new ServiceException(ErrorCode.PAYMENT_NOT_COMPLETED);
        }

        // 5. 금액 검증 (DB 저장 금액 vs 포트원 실제 결제 금액)
        if (portOneData.getAmount().getTotal() != payment.getPaymentPrice()) {
            throw new ServiceException(ErrorCode.PAYMENT_FORGERY_DETECTED);
        }

        // 6. 재고 차감
        payment.stateUpdate(PaymentStatus.PAID);
        orderService.stockReduce(payment.getOrder());
    }

    private void handleSecurityIssue(String impUid, String token) {
        // 환불 API 호출
        log.error("err: 금액 불일치 imp_uid: {}", impUid);
    }
}
