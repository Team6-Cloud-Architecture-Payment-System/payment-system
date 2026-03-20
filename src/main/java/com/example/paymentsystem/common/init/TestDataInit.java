package com.example.paymentsystem.common.init;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.dto.request.SignUpRequest;
import com.example.paymentsystem.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInit implements ApplicationRunner {

    private final AuthService authService;

    @Override
    public void run(ApplicationArguments args) {
        // 프론트 로그인 페이지에 표시된 테스트 계정
        String name = "임하은";
        String email = "admin@test.com";
        String password = "admin";

        try {
            authService.signUp(new SignUpRequest(name, email, password));
            log.info("임시 회원 생성 완료: {}", email);
        } catch (ServiceException e) {
            // 이미 가입된 경우 서버 시작이 실패하지 않도록 무시
            if (ErrorCode.DUPLICATED_EMAIL.getMessage().equals(e.getMessage())) {
                log.info("임시 회원이 이미 존재합니다: {}", email);
                return;
            }

            throw e;
        }
    }
}
