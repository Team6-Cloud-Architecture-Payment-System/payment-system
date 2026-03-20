package com.example.paymentsystem.domain.refund.service;

import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.order.entity.OrderStatus;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.point.service.PointHistoryService;
import com.example.paymentsystem.domain.refund.dto.*;
import com.example.paymentsystem.domain.refund.entity.Refund;
import com.example.paymentsystem.domain.refund.entity.RefundStatus;
import com.example.paymentsystem.domain.refund.repository.RefundRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final PointHistoryService pointHistoryService;
    private final UserRepository userRepository;


    @Transactional
    public CreateRefundResponse save(Long paymentId, CreateRefundRequest request, Long userId) {
        // 결제 건이 존재하는지 확인
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(
                () -> new EntityNotFoundException("결제 건이 존재하지 않습니다.")
        );
        // JWT토큰에서 userId 꺼내서 소유권 검증
        if (!payment.getOrder().getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 결제건에 대한 권한이 없습니다.");
        }

        // 결제 상태 검증
        if (payment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("결제 완료 상태가 아니면 환불할 수 없습니다.");
        }
        // 주문 상태 검증
        if (payment.getOrder().getOrderStatus() != OrderStatus.ORDER_CONFIRMED) {
            throw new IllegalStateException("주문 확정 상태가 아니면 환불할 수 없습니다.");
        }

        // 이미 환불 레코드가 존재하는지
        if (refundRepository.existsByPayment(payment)) {
            throw new IllegalStateException("이미 환불된 결제건입니다.");
        }

        //TODO PortOne 취소 API 호출


        // 환불 레코드 엔티티 내부로 캡슐화
        Refund refund = Refund.of(payment, request.refundReason());

        Refund savedRefund = refundRepository.save(refund);

        // user 변수 미리 선언
        User user = payment.getOrder().getUser();

        // 결제 상태 변경
        payment.stateUpdate(PaymentStatus.REFUNDED);

        //TODO 주문 상태 변경
//        payment.getOrder().updateStatus(OrderStatus.REFUNDED);

        // 포인트 복구 처리
        if (payment.getOrder().getUsedPoint() > 0) {
            pointHistoryService.restorePoint(user, payment.getOrder(), payment.getOrder().getUser().getPoint());
        }

        //TODO 멤버십 등급 재계산

        return new CreateRefundResponse(savedRefund);
    }

    // 특정 주문 환불 내역 조회
    @Transactional(readOnly = true)
    public GetOrderRefundResponse getRefund(Long orderId, Long userId) {
        Refund refund = refundRepository.findByPaymentOrderId(orderId).orElseThrow(
                () -> new EntityNotFoundException("해당 주문의 환불 내역이 없습니다.")
        );
        // 본인 주문건이 맞는지 확인
        if (!refund.getPayment().getOrder().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 주문에 대한 환불내역 확인 권한이 없습니다.");
        }
        return new GetOrderRefundResponse(refund);
    }

    // 유저 개인 환불 내역 전체 조회
    @Transactional(readOnly = true)
    public GetMyRefundListResponse getMyRefundList(Long userId, Pageable pageable) {
        // 토큰으로 해당 유저의 조회가 맞는지 검증은 끝남,

        // 유저의 존재 여부 확인
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 존재하지 않습니다.")
        );
        // 유저의 전체 환불내역 조회 페이징
        Page<Refund> refundPage = refundRepository.findAllByPaymentOrderUser(user, pageable);

        // DTO 변환 후 반환
        List<RefundSummaryResponse> refundList = refundPage.getContent().stream()
                .map(RefundSummaryResponse::new)
                .toList();

        return new GetMyRefundListResponse(
                refundList,
                refundPage.getNumber() + 1,
                refundPage.getSize(),
                refundPage.getTotalElements(),
                refundPage.getTotalPages()
        );
    }
}
