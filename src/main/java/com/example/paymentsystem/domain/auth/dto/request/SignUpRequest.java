package com.example.paymentsystem.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "이름은 필수 입력 값입니다.") String name,
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.") String email,
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.") String password,
        @NotBlank(message = "전화번호는 필수 입력 값입니다.")
        @Pattern(
                regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
                message = "하이픈(-)을 제외한 올바른 전화번호 형식이어야 합니다."
        )
                String phoneNumber
) {
}
