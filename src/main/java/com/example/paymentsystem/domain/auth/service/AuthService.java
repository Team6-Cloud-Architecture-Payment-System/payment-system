package com.example.paymentsystem.domain.auth.service;

import com.example.paymentsystem.common.config.JwtTokenProvider;
import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.dto.request.LogInRequest;
import com.example.paymentsystem.domain.auth.dto.request.SignUpRequest;
import com.example.paymentsystem.domain.auth.dto.response.SignUpResponse;
import com.example.paymentsystem.domain.auth.dto.response.TokenResponse;
import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입 로직
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        //이메일 중복 검사
        if(userRepository.existsByEmail(request.email())) {
            throw new ServiceException(ErrorCode.DUPLICATED_EMAIL,ErrorCode.DUPLICATED_EMAIL.getMessage());
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // 암호화 적용
                .build();

        User savedUser = userRepository.save(user);

        return SignUpResponse.from(savedUser);
    }
    //로그인 로직
    @Transactional
    public TokenResponse login(LogInRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ServiceException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));

        if(!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ServiceException(ErrorCode.WRONG_PASSWORD, ErrorCode.WRONG_PASSWORD.getMessage());
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId());

        return new TokenResponse(accessToken, "Bearer");
    }
    //유저 조회로직
}
