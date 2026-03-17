package com.example.paymentsystem.common.exception;

import lombok.Getter;

@Getter

public class RefundException extends RuntimeException {
    private final ErrorCode errorCode;

    public RefundException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
