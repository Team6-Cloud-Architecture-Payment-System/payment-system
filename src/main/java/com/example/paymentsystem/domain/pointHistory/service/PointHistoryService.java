package com.example.paymentsystem.domain.pointHistory.service;

import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.repository.OrderRepository;
import com.example.paymentsystem.domain.pointHistory.dto.GetPointTransactionHistory;
import com.example.paymentsystem.domain.pointHistory.entity.PointHistory;
import com.example.paymentsystem.domain.pointHistory.entity.Type;
import com.example.paymentsystem.domain.pointHistory.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

//    @Transactional(readOnly = true)
//    public GetMyPointHistoryResponse getMyPoint(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow(
//                () -> new IllegalArgumentException("유저를 찾지 못했습니다.")
//        );
//        return new GetMyPointHistoryResponse(user.getId(), user.getPoint());
//    }

    // 현재 포인트 잔액 계산 (합산)
    public Long calculatorPoint(User user) {
        Long totalPoint = pointHistoryRepository.sumPointByUser(user);
        return totalPoint == null ? 0L : totalPoint;
    }

    // 포인트 적립 (결제 성공 시)
    @Transactional
    public void earnPoint(User user, Order order, Long paymentPrice, Double rewardRate) {
        // rewardRate는 Double 타입이므로, long타입으로 형변환 후 받아줌
        Long earnPrice = (long) (paymentPrice * rewardRate);

        PointHistory earnedPoint = new PointHistory(earnPrice, Type.EARNED, user, order);

        pointHistoryRepository.save(earnedPoint);

        user.updatePoint(earnPrice);
    }

    // 포인트 사용 (결제 시)
    @Transactional
    public void usePoint(User user, Order order, Long price) {
        // 잔액이 부족한지 체크
        Long currentPrice = calculatorPoint(user);
        if (currentPrice < price) {
            throw new IllegalStateException("포인트 잔액이 부족합니다.");
        }
        PointHistory spentPoint = new PointHistory(-price, Type.SPENT, user, order);
        pointHistoryRepository.save(spentPoint);

        user.updatePoint(price);
    }

    // 포인트 복구
    @Transactional
    public void restorePoint(User user, Order order, Long price) {
        // 1. 해당 주문이 SPENT 상태인지, 거래 내역 확인
        pointHistoryRepository.findByOrderAndType(order, Type.SPENT).orElseThrow(
                () -> new IllegalStateException("사용한 포인트 내역이 없습니다.")
        );
        // 2. RESTORED 거래 내역 생성
        PointHistory restoredPoint = new PointHistory(
                price, // 복구할 금액
                Type.RESTORED,
                user,
                order
        );
        pointHistoryRepository.save(restoredPoint);

        user.updatePoint(price);
    }

    // 포인트 적립 취소 (환불 시)
    @Transactional
    public void cancelPoint(User user, Order order) {
        // 거래 내역 확인
        PointHistory earnedPoint = pointHistoryRepository.findByOrderAndType(order, Type.EARNED).orElseThrow(
                () -> new IllegalStateException("적립된 포인트 내역이 없습니다.")
        );
        PointHistory cancelledPoint = new PointHistory(
                -earnedPoint.getPoint(),
                Type.CANCELLED,
                user,
                order
        );
        pointHistoryRepository.save(cancelledPoint);

        user.updatePoint(-earnedPoint.getPoint());
    }

    // 포인트 소멸
    @Transactional
    public void expiredPoint(User user, Long price){
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
    public Page<GetPointTransactionHistory> getPointTransactionHistory(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("유저가 존재하지 않습니다.")
        );
        return pointHistoryRepository.findAllByUser(user, pageable);

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
