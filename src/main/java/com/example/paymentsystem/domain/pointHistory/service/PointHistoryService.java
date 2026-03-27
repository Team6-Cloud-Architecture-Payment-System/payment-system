package com.example.paymentsystem.domain.pointHistory.service;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.membership.repository.MembershipTierRepository;
import com.example.paymentsystem.domain.membership.repository.UserMembershipRepository;
import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.entity.OrderStatus;
import com.example.paymentsystem.domain.order.repository.OrderRepository;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.pointHistory.dto.GetPointTransactionHistory;
import com.example.paymentsystem.domain.pointHistory.dto.PointHistorySummaryResponse;
import com.example.paymentsystem.domain.pointHistory.entity.PointHistory;
import com.example.paymentsystem.domain.pointHistory.entity.Type;
import com.example.paymentsystem.domain.pointHistory.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final PaymentRepository paymentRepository;

    private static final Long MIN_USE_POINT = 1000L; // 예: 1000원부터 사용 가능

    /*현재 포인트 잔액 계산(합산)

    public Long calculatorPoint(User user) {
        Long totalPoint = pointHistoryRepository.sumPointByUser(user);
        return totalPoint == null ? 0L : totalPoint;
    }*/

    // 포인트 적립 (주문 완료시 시)
    @Transactional
    public void earnPoint(User user, Order order) {
        // 이미 적립된 내역이 있는지 확인
        if (pointHistoryRepository.existsByOrderAndType(order, Type.EARNED)) {
            return; // 이미 적립되어있으면 그냥 리턴
        }
        // 결제 상태 확인
        // 2. 상태 검증 (결제 완료 + 주문 확정 상태여야 함)
        // 기존 코드의 논리 오류 수정 (PAID 상태 '여야' 함)
        boolean isPaid = paymentRepository.existsByOrderAndPaymentStatus(order, PaymentStatus.PAID);
        if (!isPaid || order.getOrderStatus() != OrderStatus.ORDER_CONFIRMED) {
            throw new ServiceException(ErrorCode.INVALID_ORDER_STATUS);
        }
        // 현재 등급에 해당되는 등급 정책 조회
        Double rewardRate = membershipTierRepository.findRewardRateByUserId(user.getId())
                .orElseThrow(() -> new ServiceException(ErrorCode.MEMBERSHIP_NOT_FOUND));
        // rewardRate는 Double 타입이므로, long타입으로 형변환 후 받아줌
        Long earnPrice = (long) Math.floor(order.getPaymentPrice() * rewardRate);
        // order.getTotalPrice() -> order.getPaymentPrice()로 변경

        PointHistory earnedPoint = new PointHistory(earnPrice, Type.EARNED, user, order);

        pointHistoryRepository.save(earnedPoint);

        user.updatePoint(earnPrice);
    }

    // 포인트 사용 (결제 시)
    @Transactional
    public void usePoint(Long userId, Order order) {
        Long usedPoint = order.getUsedPoint();

        if(usedPoint == 0) {
            return;
        }

        if (pointHistoryRepository.existsByOrderAndType(order, Type.SPENT)) {
            return; // 이미 차감 이력이 있다면 중복 실행 방지
        }


        if (usedPoint < MIN_USE_POINT) {
            throw new ServiceException(ErrorCode.POINT_BELOW_MINIMUM);
        }

        // 비관적 락을 통해 유저 정보 재조회 (동시성 방지)
        User user = userRepository.findByIdWithPessimisticLock(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        // 사용할 수 있는 포인트가 있는지 체크
        if (user.getPoint() < usedPoint) {
            throw new ServiceException(ErrorCode.INSUFFICIENT_POINT);
        }

        // 포인트 차감
        user.updatePoint(-usedPoint);

        // 이력 저장
        PointHistory spentPoint = new PointHistory(-usedPoint, Type.SPENT, user, order);

        pointHistoryRepository.save(spentPoint);

        userRepository.saveAndFlush(user);

    }

    // 포인트 복구
    @Transactional
    public void restorePoint(User user, Order order) {

        // 1. 복구된 내역이 이미 존재하는지 확인
        if (pointHistoryRepository.existsByOrderAndType(order, Type.RESTORED)) {
//            throw new ServiceException(ErrorCode.ALREADY_RESTORED);
            log.warn("이미 복구된 포인트 내역이 있습니다. Order ID: {}", order.getId());
            return;
        }

        Optional<PointHistory> spentPointOpt = pointHistoryRepository.findByOrderAndType(order, Type.SPENT);
        if (spentPointOpt.isEmpty()) {
            log.info("복구할 포인트 사용 내역이 없습니다. Order ID: {}", order.getId());
            return;
        }
        PointHistory spentPoint = spentPointOpt.get();

        Long restoredPrice = spentPoint.getPoint() * -1;
        // 복구는 +(양수)값이 나와야 하는데, userPoint(포인트 사용)에서 -price로 값을 넘겨주고 있으니, -1을 곱해줌

        // 2. 포인트 복구 내역 생성
        PointHistory restoredPoint = new PointHistory(
                restoredPrice, // 복구할 금액
                Type.RESTORED,
                user,
                order
        );
        pointHistoryRepository.save(restoredPoint);

        user.updatePoint(restoredPrice);
    }

    // 포인트 적립 취소 (환불 시)
    @Transactional
    public void cancelPoint(User user, Order order) {
        if (pointHistoryRepository.existsByOrderAndType(order, Type.CANCELLED)) {
            return; // 이미 취소됨
        }

        Optional<PointHistory> earnedPointOpt = pointHistoryRepository.findByOrderAndType(order, Type.EARNED);

        if (earnedPointOpt.isEmpty()) {
            log.info("취소할 포인트 내역이 없습니다. Order ID: {}", order.getId());
            return;
        }

        PointHistory earnedPoint = earnedPointOpt.get();

        Long cancelPrice = earnedPoint.getPoint() * -1;

        PointHistory cancelHistory = new PointHistory(
                cancelPrice,
                Type.CANCELLED,
                user,
                order
        );
        pointHistoryRepository.save(cancelHistory);

        // 유저의 포인트 차감
        user.updatePoint(cancelPrice);
    }

    // 포인트 소멸
    @Transactional
    public void expiredPoint(User user, Long price) {
        PointHistory expiredPoint = new PointHistory(
                -price,
                Type.EXPIRED,
                user,
                null
        );
        pointHistoryRepository.save(expiredPoint);

        user.updatePoint(-price);
    }

    // 포인트 거래 내역 조회
    @Transactional(readOnly = true)
    public GetPointTransactionHistory getPointTransactionHistory(Long userId, Pageable pageable) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ServiceException(ErrorCode.USER_NOT_FOUND)
        );

        Page<PointHistory> page = pointHistoryRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);

        List<PointHistorySummaryResponse> pointHistoryList = page.getContent().stream()
                .map(PointHistorySummaryResponse::new)
                .toList();

        return new GetPointTransactionHistory(
                pointHistoryList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}

/* User.user_point      → 현재 잔액 (스냅샷)
   Point.point          → 변동액 (이력)

   은행 통장으로 비유하면

   User.user_point = 통장 잔액
    └─> 지금 내가 가진 포인트 총합
    └─> 항상 최신 값으로 업데이트

Point 테이블 = 입출금 내역
    └─> 언제 얼마가 들어오고 나갔는지 기록
    └─> 변동액만 기록 (+500, -300 등)

   */
