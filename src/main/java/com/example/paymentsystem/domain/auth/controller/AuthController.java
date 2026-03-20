package com.example.paymentsystem.domain.auth.controller;

import com.example.paymentsystem.common.config.JwtTokenProvider;
import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.auth.dto.request.LogInRequest;
import com.example.paymentsystem.domain.auth.dto.request.SignUpRequest;
import com.example.paymentsystem.domain.auth.dto.response.LogOutResponse;
import com.example.paymentsystem.domain.auth.dto.response.SignUpResponse;
import com.example.paymentsystem.domain.auth.dto.response.TokenResponse;
import com.example.paymentsystem.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                .ok(ApiResponse.success(response));
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
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
