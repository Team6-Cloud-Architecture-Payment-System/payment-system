package com.example.paymentsystem.domain.payment.service;

import com.example.paymentsystem.domain.payment.dto.PaymentTryRequest;
import com.example.paymentsystem.domain.payment.dto.PaymentTryResponse;
import com.example.paymentsystem.domain.payment.entity.Payment;
import com.example.paymentsystem.domain.payment.repository.PaymentRepository;
import com.example.paymentsystem.domain.payment.status.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentTryResponse tryPayment(Long orderId, PaymentTryRequest request) {

//        Order order = orderRepository.findById(orderId);

        return new PaymentTryResponse(paymentRepository.save(new Payment(
                orderId,
                request.payments_id(),
                PaymentStatus.WAIT,
                request.payment_price())));
    }
}

