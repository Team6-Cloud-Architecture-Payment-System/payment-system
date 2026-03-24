package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.payment.dto.WebhookRequestDto;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.entity.PaymentStatus;
import com.example.paymentsystem.domain.payment.entity.Webhook;
import com.example.paymentsystem.domain.payment.entity.WebhookStatus;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final WebhookRepository webhookRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;  // confirmPayment() 호출용

    //예외 발생해도 롤백처리되지 않게 처리
    @Transactional(noRollbackFor = {ServiceException.class, IllegalArgumentException.class})
    public void receiveWebhook(WebhookRequestDto dto, String webhookId) {

        // 멱등성 체크 : webhook_id 중복 체크 (멱등성) 이미 처리된 웹훅이면 스킵
        if (webhookId != null && webhookRepository.existsByWebhookId(webhookId)) {
            log.info("[Webhook 중복] 이미 처리된 webhookId={}", webhookId);
            return;
        }

        // RECEIVED로 저장
        Webhook webhook = Webhook.of(dto, webhookId);
        webhook = webhookRepository.saveAndFlush(webhook);

        try {
            String eventType = dto.eventStatus();
            //결제 완료시 확정처리
            if ("Transaction.Paid".equals(eventType)) {
                paymentService.confirmPayment(dto.data().paymentId());

            } else if ("Transaction.Cancelled".equals(eventType)) {
                Payment payment = paymentRepository.findByPaymentId(dto.data().paymentId())
                        .orElseThrow(() -> new ServiceException(ErrorCode.WEBHOOK_PAYMENT_NOT_FOUND));

                if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
                    log.info("[Webhook 취소확인] 이미 환불 처리된 paymentId={}", dto.data().paymentId());
                } else {
                    log.warn("[Webhook 취소 누락] DB 미반영 감지, paymentId={}", dto.data().paymentId());
                    payment.stateUpdate(PaymentStatus.REFUNDED);
                }
            }

            webhook.statusUpdate(WebhookStatus.PROCESSED);
            log.info("[Webhook 처리 완료] type={}, paymentId={}", eventType, dto.data().paymentId());

        } catch (Exception e) {
            webhook.statusUpdate(WebhookStatus.FAILED);
            log.error("[Webhook 처리 실패] webhookId={}, error={}", webhookId, e.getMessage());
        }
    }
}
