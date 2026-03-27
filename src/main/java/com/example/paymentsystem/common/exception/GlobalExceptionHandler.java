package com.example.paymentsystem.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j

public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("[MethodArgumentNotValidException] path: {}, message: {}", request.getRequestURI(), ex.getMessage(), ex);

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.ofValid(errorMessage, request.getRequestURI()));
    }

    @ExceptionHandler(RefundException.class)
    public ResponseEntity<?> handleRefundException(
            RefundException ex, HttpServletRequest request) {
        log.warn("[RefundException] path: {}, message: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(ex.getErrorCode().getStatus())
                .body(ErrorResponse.of(ex.getErrorCode(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, HttpServletRequest request) {
        log.warn("[Exception] path: {}, message: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of500(ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleServiceException(ServiceException ex, HttpServletRequest request) {
        log.warn("[ServiceException] path: {}, message: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ex.getErrorCode(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("[IllegalArgumentException] path: {}, message: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of500(ex.getMessage(), request.getRequestURI()));
    }
}
