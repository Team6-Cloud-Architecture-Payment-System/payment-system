package com.example.paymentsystem.domain.order.service;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.order.dto.CreateOrderItemRequest;
import com.example.paymentsystem.domain.order.dto.CreateOrderRequest;
import com.example.paymentsystem.domain.order.dto.CreateOrderResponse;
import com.example.paymentsystem.domain.order.dto.OrderHistoryResponse;
import com.example.paymentsystem.domain.order.entity.Order;
import com.example.paymentsystem.domain.order.entity.OrderItem;
import com.example.paymentsystem.domain.order.entity.OrderStatus;
import com.example.paymentsystem.domain.order.repository.OrderRepository;
import com.example.paymentsystem.domain.product.entity.Product;
import com.example.paymentsystem.domain.product.entity.ProductStatus;
import com.example.paymentsystem.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 주문 생성
    @Transactional
    public CreateOrderResponse create(Long userId, CreateOrderRequest request) {

        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND));

        // 주문 상품이 비어있으면 예외 처리
        if (request.orderItems() == null || request.orderItems().isEmpty()) {
            throw new ServiceException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 사용 포인트 가져오기
        long usedPoint = request.usedPoint();

        // 유저가 가진 포인트가 null이면 0으로 처리
        long userPoint = user.getPoint() == null ? 0L : user.getPoint();

        // 유저가 포인트를 정말 그만큼 들고 있는지 확인
        if (userPoint < usedPoint) {
            throw new ServiceException(ErrorCode.INSUFFICIENT_POINT);
        }

        // 주문 아이템들을 담을 리스트 생성
        List<OrderItem> orderItems = new ArrayList<>();

        // 상품 총액(포인트 차감 전) 초기화
        long totalPrice = 0L;

        // 요청으로 들어온 주문 상품들 하나씩 확인
        for (CreateOrderItemRequest itemRequest : request.orderItems()) {

            // 상품이 정말로 존재하는지 확인
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ServiceException(ErrorCode.PRODUCT_NOT_FOUND));

            // 상품 상태가 판매중인지 확인
            if (product.getStatus() != ProductStatus.FOR_SALE) {
                throw new ServiceException(ErrorCode.PRODUCT_NOT_ON_SALE);
            }

            // 있으면 재고가 남아있는지 확인
            if (product.getStock() < itemRequest.quantity()) {
                throw new ServiceException(ErrorCode.INSUFFICIENT_STOCK);
            }

            // 주문 아이템 생성
            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    itemRequest.quantity()
            );

            // 생성한 주문 아이템을 리스트에 추가
            orderItems.add(orderItem);

            // 주문 상품 총액 누적
            totalPrice += orderItem.getSubtotalPrice();
        }

        // 결제 금액 계산
        long paymentPrice = totalPrice - usedPoint;

        // 사용 포인트가 총 주문 금액보다 크면 예외 처리
        if (paymentPrice < 0) {
            throw new ServiceException(ErrorCode.INVALID_USED_POINT);
        }

        // 주문 생성
        Order order = new Order(
                user,
                totalPrice,
                usedPoint,
                paymentPrice
        );

        // 주문에 주문 아이템들 연결
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        // 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 주문 생성 응답 반환
        return new CreateOrderResponse(savedOrder);
    }


    // 주문 내역 조회
    public OrderHistoryResponse getMyOrders(Long userId, Pageable pageable) {

        // 로그인한 사용자의 주문 목록을 조회
        Page<Order> orderPage = orderRepository.findByUserId(userId, pageable);

        // 조회한 주문 엔티티 페이지를 주문 내역 응답 DTO로 변환
        return OrderHistoryResponse.from(orderPage);
    }

    // 주문 상세 조회


    // 주문 확정 (수동)
    @Transactional
    public void confirmOrder(Long userId, Long orderId) {

        // 해당 주문이 존재하는지 + 내 주문이 맞는지 확인
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ServiceException(ErrorCode.ORDER_NOT_FOUND));

        // 주문 확정
        order.confirm();

        // TODO: 주문 확정 시 포인트 지급 로직 연결
    }

    // 5일 뒤면 자동으로 주문 확정
    @Transactional
    @Scheduled(cron = "0 0 6 * * *")
    public void autoConfirmOrders() {
        LocalDateTime targetTime = LocalDateTime.now().minusDays(5);

        List<Order> orders = orderRepository.findOrdersReadyForAutoConfirm(targetTime);

        for (Order order : orders) {
            order.confirm();
            // TODO: 자동 주문 확정 시 포인트 지급 로직 연결
        }
    }

    // 환불 : 환불에서 완료, 결제대기->주문완료 : 결제에서 완료



}