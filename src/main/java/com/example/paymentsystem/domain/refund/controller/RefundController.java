//package com.example.paymentsystem.domain.refund.controller;
//
//import com.example.paymentsystem.common.dto.ApiResponse;
//import com.example.paymentsystem.domain.refund.dto.CreateRefundRequest;
//import com.example.paymentsystem.domain.refund.dto.CreateRefundResponse;
//import com.example.paymentsystem.domain.refund.dto.GetRefundResponse;
//import com.example.paymentsystem.domain.refund.service.RefundService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api")
//public class RefundController {
//    private final RefundService refundService;
//
//    @PostMapping("/payments/{paymentId}/refunds")
//    public ResponseEntity<ApiResponse<CreateRefundResponse>> createRefund(
//            @PathVariable Long paymentId, @Valid @RequestBody CreateRefundRequest request) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(refundService.save(paymentId, request)));
//    }
//
//    // @AuthenticationPrincipal >> SecurityContextHolder에 저장된 JWT 토큰을 꺼냄,
//    // 로그인한 인증된 유저의 정보를 꺼내서 주입받을 때 사용, Session방식에서 @SessionAttribute와 유사함
//    // (expression = "id")는 SecurityContext에 저장된 객체 내부에서 id라는 이름의 필드를 찾아서 그 값만 쏙 뽑아오라는 의미
//    @GetMapping("/orders/{orderId}/refunds")
//    public ResponseEntity<ApiResponse<GetRefundResponse>> getRefund(
//            @PathVariable Long orderId,
//            @RequestParam Long userId) {
//        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(refundService.getRefund(orderId, userId)));
//    }
//
////    @GetMapping("/refunds/me")
////    public ResponseEntity<ApiResponse<>>
//
//}
