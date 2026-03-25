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
import com.example.paymentsystem.domain.pointHistory.repository.PointHistoryRepository;
import com.example.paymentsystem.domain.pointHistory.service.PointHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
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
    private final PointHistoryService pointHistoryService;

    @Transactional
    public PaymentTryResponse tryPayment(Long orderId) {

        // 1. 존재하는 주문인지 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));

        // 2. 결제 가능한 주문 상태인지 검증
        if (!order.getOrderStatus().equals(OrderStatus.PAYMENT_PENDING)) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }


        // 4. 결제 식별자 생성 및 저장
        String generatedPaymentId = paymentIdGenerator.generate(String.valueOf(order.getUser().getId()));
        Payment saved = new Payment(
                order,
                generatedPaymentId,
                PaymentStatus.PENDING,
                order.getPaymentPrice()
        );

        return new PaymentTryResponse(paymentRepository.saveAndFlush(saved));
    }


    @Transactional
    public void confirmPayment(String paymentId) {

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ServiceException(ErrorCode.PAYMENT_NOT_FOUND));

        // 중복 처리 방지
        if (payment.getPaymentStatus() == PaymentStatus.PAID){
            return;
        }

        // processPaymentSuccess 공통 로직을 만들어서 따로 뺌
        if (payment.getPaymentPrice() == 0) {
            processPaymentSuccess(payment);
            return;
        }

        // 포트원 검증
        PortOneVerificationResponseDto portOneData = portApiService.getVerifyPayment(paymentId);

        // 4. 포트원 결제 상태 확인 (PAID 여부)
        if (!portOneData.getStatus().equals(PaymentStatus.PAID.toString())) {
            throw new ServiceException(ErrorCode.PAYMENT_NOT_COMPLETED);
        }

        // 5. 금액 검증 (DB 저장 금액 vs 포트원 실제 결제 금액)
        if (portOneData.getAmount().getTotal() != payment.getPaymentPrice()) {
            throw new ServiceException(ErrorCode.PAYMENT_FORGERY_DETECTED);
        }

        // 검증 완료 후 공통 성공 로직 실행
        processPaymentSuccess(payment);
    }

    // 성공 시 공통 로직 (포인트 차감 코드는 여기서 빠짐)
    private void processPaymentSuccess(Payment payment) {
        Order order = payment.getOrder();
        // 결제 시도 시점에 포인트를 미리 깎기
        if (order.getUsedPoint() != null && order.getUsedPoint() > 0) {
            pointHistoryService.usePoint(order.getUser().getId(), order);
        }

        // 3.1 주문에 대한 PENDING 상태가 있는지 찾아보기
        paymentRepository.findByOrderAndPaymentStatus(order, PaymentStatus.PENDING)
                // forEach -> List에서 하나씩 꺼내서 반복문을 돌림, p -> 찾아낸 Payment 객체
                // 만약 PENDING 상태인 결제건이 3개면 3번 반복하여 각각의 상태를 FAIL로 바꿈
                .forEach(p -> p.stateUpdate(PaymentStatus.FAIL));

        payment.stateUpdate(PaymentStatus.PAID);
        orderService.stockReduce(payment.getOrder());
        orderService.completeOrder(payment.getOrder());
    }



    private void handleSecurityIssue(String impUid, String token) {
        // 환불 API 호출
        log.error("err: 금액 불일치 imp_uid: {}", impUid);
    }
}
