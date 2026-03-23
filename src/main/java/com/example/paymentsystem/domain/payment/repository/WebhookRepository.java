package com.example.paymentsystem.domain.payment.repository;

import com.example.paymentsystem.domain.payment.entity.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {

    boolean existsByPaymentId(String paymentId);

    boolean existsByWebhookId(String webhookId);
}
