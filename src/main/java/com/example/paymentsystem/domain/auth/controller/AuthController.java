package com.example.paymentsystem.domain.auth.controller;

import com.example.paymentsystem.common.config.JwtTokenProvider;
import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.dto.request.LogInRequest;
import com.example.paymentsystem.domain.auth.dto.request.SignUpRequest;
import com.example.paymentsystem.domain.auth.dto.response.LogOutResponse;
import com.example.paymentsystem.domain.auth.dto.response.SignUpResponse;
import com.example.paymentsystem.domain.auth.dto.response.TokenResponse;
import com.example.paymentsystem.domain.auth.dto.response.UserInfoResponse;
import com.example.paymentsystem.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp (@Valid @RequestBody SignUpRequest request) {
        SignUpResponse response = authService.signUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LogInRequest request) {
        TokenResponse response = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogOutResponse>> logout(@RequestHeader("Authorization") String token) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        boolean isValid = jwtTokenProvider.validateToken(accessToken);
        LogOutResponse response;
        if(isValid) {
            response = new LogOutResponse(true, "로그아웃 성공");
        }else {
            response = new LogOutResponse(false, "로그아웃 실패");
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfo(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            throw new ServiceException(ErrorCode.UNAUTHORIZED);
        }

        // 2. 서비스 레이어 호출 (ID를 넘겨서 DB 조회 및 DTO 변환)
        UserInfoResponse response = authService.userInfo(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response));

    }
}
