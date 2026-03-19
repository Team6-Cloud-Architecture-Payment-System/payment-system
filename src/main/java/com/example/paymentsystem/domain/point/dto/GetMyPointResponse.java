//package com.example.paymentsystem.domain.point.dto;
//
//
//import com.example.paymentsystem.domain.auth.entity.User;
//
//public record GetMyPointResponse(
//        Long userId,
//        Long point
//) {
//    public GetMyPointResponse(User user) {
//        this(
//                user.getId(),
//                user.getPoint()
//        );
//    }
//}
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
