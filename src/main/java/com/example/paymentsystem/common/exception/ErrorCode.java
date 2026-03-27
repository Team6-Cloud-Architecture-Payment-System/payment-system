package com.example.paymentsystem.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // payment
    PORTONE_AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "포트원 인증에 실패했습니다."), // 401
    PORTONE_FORBIDDEN(HttpStatus.FORBIDDEN, "포트원 API 접근 권한이 없습니다."), // 403
    ALREADY_CANCELLED_PAYMENT(HttpStatus.CONFLICT, "이미 취소된 결제 건입니다."), // 409
    PORTONE_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 포트원 API 오류가 발생했습니다."),
    PORTONE_SERVER_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "포트원 서버 연결에 실패했습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND,"결제 내역을 찾을 수 없습니다."),
    ALREADY_PAID(HttpStatus.BAD_REQUEST,"이미 완료된 결제입니다."),
    PAYMENT_FORGERY_DETECTED(HttpStatus.CONFLICT, "결제 금액 위변조가 감지되었습니다."),
    PAYMENT_NOT_COMPLETED(HttpStatus.valueOf(422), "결제가 완료되지 않은 상태입니다."),
    WEBHOOK_PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "웹훅 처리 중 결제 정보를 찾을 수 없습니다."),
    // auth
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일 입니다."),
    DUPLICATED_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "이미 가입된 전화번호 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다. 다시 인증을 시도해 주세요."),
    //product
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    // order
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "보유 포인트가 부족합니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_NOT_ON_SALE(HttpStatus.BAD_REQUEST, "판매 중인 상품만 주문할 수 있습니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "상품의 재고가 부족합니다."),
    INVALID_USED_POINT(HttpStatus.BAD_REQUEST, "사용 포인트가 올바르지 않습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "주문 상품은 최소 1개 이상이어야 합니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "주문 확정은 주문 완료에서만 가능합니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문을 찾을 수 없습니다."),
    // point
    POINT_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "사용한 포인트 내역이 없습니다."),
    EARNED_POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "적립된 포인트 내역이 없습니다."),

    // user

    // refund
    REFUND_NO_AUTHORITY(HttpStatus.FORBIDDEN,  "해당 환불에 권한이 없습니다."),
    ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "이미 환불 처리가 완료된 결제건입니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "결제 완료 상태인 경우에만 환불이 가능합니다."),
    REFUND_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문의 환불 내역이 없습니다."),
    ALREADY_RESTORED(HttpStatus.BAD_REQUEST, "이미 복구된 내역이 존재합니다."),

    // membership

    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "멤버십 정보가 없습니다."),
    // pointhistory
    POINT_BELOW_MINIMUM(HttpStatus.BAD_REQUEST, "포인트 최소 사용금액은 1000원입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
