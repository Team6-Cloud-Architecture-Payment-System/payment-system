package com.example.paymentsystem.domain.order.controller;
import com.example.paymentsystem.domain.order.dto.CreateOrderRequest;
import com.example.paymentsystem.domain.order.dto.CreateOrderResponse;
import com.example.paymentsystem.domain.order.service.OrderService;
import com.sun.security.auth.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        CreateOrderResponse response = orderService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
