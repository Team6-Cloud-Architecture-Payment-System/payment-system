package com.example.paymentsystem.domain.refund.service;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.membership.service.MembershipService;
import com.example.paymentsystem.domain.order.entity.OrderStatus;
import com.example.paymentsystem.domain.payment.dto.CancelRequestDto;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.service.PortOneService;
import com.example.paymentsystem.domain.pointHistory.service.PointHistoryService;
import com.example.paymentsystem.domain.refund.dto.*;
import com.example.paymentsystem.domain.refund.entity.Refund;
import com.example.paymentsystem.domain.refund.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final PointHistoryService pointHistoryService;
    private final UserRepository userRepository;
    private final MembershipService membershipService;
    private final PortOneService  portOneService;


    @Transactional
    public CreateRefundResponse createRefundRequest(String paymentId, CreateRefundRequest request, Long userId) {
        // 결제 건이 존재하는지 확인
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElseThrow(
                () -> new ServiceException(ErrorCode.PAYMENT_NOT_FOUND)
        );
        validateRefundAuthorization(payment, userId);
        validatePaymentStatus(payment);
        validateOrderStatus(payment);
        validateAlreadyRefundRecord(payment);

        // 환불 레코드 엔티티 내부로 캡슐화
        Refund refund = Refund.of(payment, request.refundReason());

        Refund savedRefund = refundRepository.save(refund);

        // user 변수 미리 선언
        User user = payment.getOrder().getUser();

        // 결제 상태 변경
        payment.stateUpdate(PaymentStatus.REFUNDED);

        // 주문 상태 변경
        payment.getOrder().updateStatus(OrderStatus.REFUNDED);

        // 포인트 복구 처리
        if (payment.getOrder().getUsedPoint() > 0) {
            pointHistoryService.restorePoint(user, payment.getOrder());
        }

        // 포인트 적립 취소 처리
        if(payment.getOrder().getUsedPoint() == null || payment.getOrder().getUsedPoint() > 0) {
            pointHistoryService.cancelPoint(user, payment.getOrder());
        }

        // 멤버십 등급 재계산
        membershipService.updateMembership(user.getId(), -payment.getPaymentPrice());

        // PortOne 결제 취소 API 호출
        if(payment.getPaymentPrice() != 0) {
            try {
                CancelRequestDto requestDto = new CancelRequestDto(request.refundReason());
                portOneService.cancelPayment(payment.getPaymentId(), requestDto);
            } catch (RuntimeException e) {
                throw new ServiceException(ErrorCode.PORTONE_API_ERROR);
            }
        }

        return new CreateRefundResponse(savedRefund);

    }

    // 특정 주문 환불 내역 조회
    @Transactional(readOnly = true)
    public GetOrderRefundResponse getRefund(Long orderId, Long userId) {
        Refund refund = refundRepository.findByPaymentOrderId(orderId).orElseThrow(
                () -> new ServiceException(ErrorCode.REFUND_ORDER_NOT_FOUND)
        );
        // 본인 주문건이 맞는지 확인
        if (!refund.getPayment().getOrder().getUser().getId().equals(userId)) {
            throw new ServiceException(ErrorCode.REFUND_NO_AUTHORITY);
        }
        return new GetOrderRefundResponse(refund);
    }

    // 유저 개인 환불 내역 전체 조회
    @Transactional(readOnly = true)
    public GetMyRefundListResponse getMyRefundList(Long userId, Pageable pageable) {
        // 토큰으로 해당 유저의 조회가 맞는지 검증은 끝남,

        // 유저의 존재 여부 확인
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ServiceException(ErrorCode.USER_NOT_FOUND)
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


    // 토큰 꺼내서 소유권 검증
    private void validateRefundAuthorization(Payment payment, Long userId) {
        if (!payment.getOrder().getUser().getId().equals(userId)) {
            throw new ServiceException(ErrorCode.REFUND_NO_AUTHORITY);
        }
    }

    // 결제 상태 검증
    private void validatePaymentStatus(Payment payment) {
        if (payment.getPaymentStatus() != PaymentStatus.PAID) {
            throw new ServiceException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    // 주문 상태 검증
    private void validateOrderStatus(Payment payment) {
        if (payment.getOrder().getOrderStatus() != OrderStatus.ORDER_COMPLETED) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }

    // 이미 환불 레코드가 존재하는지 확인
    private void validateAlreadyRefundRecord(Payment payment) {
        if (refundRepository.existsByPayment(payment)) {
            throw new ServiceException(ErrorCode.ALREADY_REFUNDED);
        }
    }
}
