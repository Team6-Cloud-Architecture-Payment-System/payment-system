package com.example.paymentsystem.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // payment

    // auth

    // order

    // point

    // user

    // refund
    REFUND_NO_AUTHORITY(HttpStatus.FORBIDDEN,  "해당 환불에 권한이 없습니다.");

    // membership

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
