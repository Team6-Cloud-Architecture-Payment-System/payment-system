package com.example.paymentsystem.domain.payment.entity;

import com.example.paymentsystem.domain.payment.dto.WebhookRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "webhooks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String webhookId ;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String eventStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WebhookStatus status;  // 우리 시스템 처리 상태 (RECEIVED/PROCESSED/FAILED)

    @Column(nullable = false)
    private LocalDateTime receptionCreateAt;

    @Column
    private LocalDateTime doneCreateAt;

    /**
     * 포트원 DTO를 받아 엔티티를 생성하는 정적 팩토리 메서드
     */
    public static Webhook of(WebhookRequestDto dto,String webhookId) {
        Webhook webhook = new Webhook();

        webhook.webhookId = webhookId;  // Header에서 받은 값
        webhook.paymentId = dto.data().paymentId();
        webhook.eventStatus= dto.eventStatus();
        webhook.status= WebhookStatus.RECEIVED;
        webhook.receptionCreateAt = LocalDateTime.now();

        return webhook;
    }

    public void statusUpdate(WebhookStatus status) {
        this.status = status;
        if (status == WebhookStatus.PROCESSED || status == WebhookStatus.FAILED) {
            this.doneCreateAt = LocalDateTime.now();
        }
    }

    public void eventStatusUpdate(String eventStatus) {
        this.eventStatus = eventStatus;
    }


}
