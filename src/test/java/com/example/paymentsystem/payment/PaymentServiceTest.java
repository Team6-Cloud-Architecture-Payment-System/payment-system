package com.example.paymentsystem.payment;

import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.service.OrderService;
import com.example.paymentsystem.domain.payment.dto.CancelRequestDto;
import com.example.paymentsystem.domain.payment.dto.PortOneVerificationResponseDto;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.service.PaymentService;
import com.example.paymentsystem.domain.payment.service.PortOneService;
import com.example.paymentsystem.domain.pointHistory.service.PointHistoryService;
import com.example.paymentsystem.domain.refund.service.RefundService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PortOneService portApiService;

    @Mock
    private OrderService orderService;

    @Mock
    private PointHistoryService pointHistoryService;

    @Mock
    private RefundService refundService; // 새롭게 추가된 의존성 Mocking

    @Test
    @DisplayName("내부 로직 실패 시 외부 취소 및 별도 트랜잭션 환불 이력이 저장되는지 테스트")
    void confirmPayment_with_refund_service_test() {
        // 1. Given: 기초 데이터 및 Mock 설정
        String paymentId = "test_payment_123";
        int amount = 10000;

        User mockUser = mock(User.class);
        Order mockOrder = mock(Order.class);
        given(mockOrder.getUser()).willReturn(mockUser);
        given(mockUser.getId()).willReturn(1L);

        PortOneVerificationResponseDto.Amount mockAmount = new PortOneVerificationResponseDto.Amount(amount);
        PortOneVerificationResponseDto mockResponse = new PortOneVerificationResponseDto(
                paymentId,
                "PAID",
                mockAmount
        );

        Payment mockPayment = spy(new Payment(mockOrder, paymentId, PaymentStatus.PENDING, (long) amount));

        // Stubbing
        given(paymentRepository.findByPaymentIdWithLock(paymentId))
                .willReturn(Optional.of(mockPayment));

        given(portApiService.getVerifyPayment(paymentId))
                .willReturn(mockResponse);

        // [에러 유도] 포인트 차감 시점에 예외 발생
        doThrow(new RuntimeException("의도적 결제 처리 실패"))
                .when(pointHistoryService).usePoint(anyLong(), any(Order.class));

        // 2. When & Then
        // 예외 전파 확인
        assertThrows(RuntimeException.class, () -> {
            paymentService.confirmPayment(paymentId);
        });

        // 3. Verify: 보상 트랜잭션 핵심 로직 검증

        // [검증 1] 외부 결제 취소 API가 호출되었는가?
        verify(portApiService, times(1))
                .cancelPayment(eq(paymentId), any(CancelRequestDto.class));

        // [검증 2] REQUIRES_NEW가 적용된 환불 서비스가 호출되었는가?
        // (이 메서드 내부에서 상태 변경과 레코드 저장이 일어남을 신뢰)
        verify(refundService, times(1))
                .saveRefundHistory(eq(mockPayment), anyString());

        // [주의] 단위 테스트에서는 REQUIRES_NEW에 의한 '실제 DB 커밋' 여부는 확인할 수 없으며,
        // 해당 서비스의 메서드가 '실행되었음'을 확인하는 것이 핵심입니다.

        System.out.println("환불 서비스 호출 및 보상 트랜잭션 검증 완료!");
    }
}