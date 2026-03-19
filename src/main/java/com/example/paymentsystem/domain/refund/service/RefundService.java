package com.example.paymentsystem.domain.refund.service;

import com.example.paymentsystem.domain.order.entity.OrderStatus;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.status.PaymentStatus;
import com.example.paymentsystem.domain.refund.dto.CreateRefundRequest;
import com.example.paymentsystem.domain.refund.dto.CreateRefundResponse;
import com.example.paymentsystem.domain.refund.dto.GetRefundResponse;
import com.example.paymentsystem.domain.refund.entity.Refund;
import com.example.paymentsystem.domain.refund.entity.RefundStatus;
import com.example.paymentsystem.domain.refund.repository.RefundRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//@Service
//@RequiredArgsConstructor
//public class RefundService {
//
//    private final RefundRepository refundRepository;
//    private final PaymentRepository paymentRepository;
//
//    @Transactional
//    public CreateRefundResponse save(Long paymentId, CreateRefundRequest request) {
//        // 1. 결제 건이 존재하는지 확인
//        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
//                () -> new EntityNotFoundException("결제 건이 존재하지 않습니다.")
//        );
//        // 2. 403 Forbidden 본인 결제건이 맞는지 체크하는 로직 필요
////        if (payment.getOrderId().equals(userId())) {
////            throw new AccessException("해당 결제건에 대한 권한이 없습니다.");
//        }
//        // 3. 400 결제 상태와 주문 상태 검증 로직 필요
//        if (payment.getStatus() != PaymentStatus.COMPLETED) {
//            throw new IllegalArgumentException("결제 완료 상태가 아니면 환불할 수 없습니다.");
//        }
//        if (payment.getOrderId().getStatus() != OrderStatus.CONFIRMED) {
//            throw new IllegalArgumentException("주문 확정 상태가 아니면 환불할 수 없습니다.");
//        }
//
//        // 4. 이미 환불 레코드가 존재하는지
//        if(refundRepository.existsByPaymentId(payment)) {
//            throw new IllegalStateException("이미 환불된 결제건입니다.");
//    }
//
//
//        // 5. 400 BadRequest 결제금액과 환불 금액이 일치하는지
//        if (!payment.getPaymentPrice().equals(request.refundPrice())) {
//            throw new IllegalArgumentException("환불 금액이 일치하지 않습니다.");
//        }
//        // 6. PortOne 호출 실패 로직
//
//        Refund refund = new Refund(
//                payment,
//                request.refundPrice(),
//                request.refundReason(),
//                RefundStatus.REFUND_COMPLETED
//        );
//
//        // JWT에서 userId 추출 후 소유권 검증
//        // PortOne 결제 취소 API 호출
//        // 결제/주문 상태 변경
//        // 포인트 복구 처리
//        // 멤버십 등급 재계산
//
//        // 결제/주문 상태 변경 업데이트 로직 추가
//
//        Refund savedRefund = refundRepository.save(refund);
//        return new CreateRefundResponse(savedRefund);
//    }
//
//    @Transactional(readOnly = true)
//    public GetRefundResponse getRefund(Long orderId, Long userId) {
//        Refund refund = refundRepository.findByPaymentOrderId(orderId).orElseThrow(
//                () -> new EntityNotFoundException("해당 주문의 환불 내역이 없습니다.")
//        );
//        // 본인 주문건이 맞는지 확인
//        if (refund.getPayment().getOrder.getUser().getId().equals(userId)) {
//            throw new IllegalArgumentException("해당 주문에 대한 환불내역 확인 권한이 없습니다.");
//        }
//        return new GetRefundResponse(refund);
//    }
//}
