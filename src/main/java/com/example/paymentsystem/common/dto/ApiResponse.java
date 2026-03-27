package com.example.paymentsystem.common.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "Request processed successfully.");
    }

    // 성공 응답 (데이터 미포함/단순 메시지)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, null, message);
    }

    // 실패 응답
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
