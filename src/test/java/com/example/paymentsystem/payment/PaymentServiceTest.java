package com.example.paymentsystem.payment;

import com.example.paymentsystem.domain.auth.entity.User; // 패키지 경로 확인 필요
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

// Static Imports (Mockito 및 Assertions)
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
    private PointHistoryService pointHistoryService; // NPE 방지를 위해 추가

    @Test
    @DisplayName("내부 로직 실패 시 보상 트랜잭션(환불)이 호출되는지 테스트")
    void confirmPayment_compensation_test() {
        // 1. Given: 기초 데이터 설정
        String paymentId = "test_payment_123";
        int amount = 10000;

        // 2. User 및 Order Mock 설정 (NPE 방지 핵심)
        User mockUser = mock(User.class);
        Order mockOrder = mock(Order.class);

        // order.getUser().getId() 가 작동하도록 스터빙
        given(mockOrder.getUser()).willReturn(mockUser);
        given(mockUser.getId()).willReturn(1L);

        // 3. PortOne 응답 DTO 생성
        PortOneVerificationResponseDto.Amount mockAmount = new PortOneVerificationResponseDto.Amount(amount);
        PortOneVerificationResponseDto mockResponse = new PortOneVerificationResponseDto(
                paymentId,
                "PAID",
                mockAmount
        );

        // 4. Payment 객체 생성 (실제 비즈니스 로직을 태우기 위해 spy 사용)
        Payment mockPayment = spy(new Payment(mockOrder, paymentId, PaymentStatus.PENDING, (long) amount));

        // 5. Mockito Behavior 설정
        given(paymentRepository.findByPaymentIdWithLock(paymentId))
                .willReturn(Optional.of(mockPayment));

        given(portApiService.getVerifyPayment(paymentId))
                .willReturn(mockResponse);

        // [보상 트랜잭션 유도] 내부 로직 중 하나에서 의도적으로 에러 발생
        // 로그 상 pointHistoryService.usePoint가 가장 먼저 호출되므로 여기서 에러 발생
        doThrow(new RuntimeException("보상 트랜잭션 테스트용 의도적 에러"))
                .when(pointHistoryService).usePoint(anyLong(), any(Order.class));

        // 6. When & Then
        // 예외가 던져지는지 확인
        assertThrows(RuntimeException.class, () -> {
            paymentService.confirmPayment(paymentId);
        });

        // 7. Then: 보상 트랜잭션(결제 취소 API)이 호출되었는지 최종 검증
        verify(portApiService, times(1))
                .cancelPayment(eq(paymentId), any(CancelRequestDto.class));

        System.out.println("보상 트랜잭션 테스트 성공!");
    }
}