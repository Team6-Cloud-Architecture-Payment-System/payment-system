package com.example.paymentsystem.domain.auth.entity;

import com.example.paymentsystem.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요.")
    private String name;

    @Column(nullable = false)
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "이메일 형식을 다시 확인해주세요.")
    private String email;

    @Column(nullable = false, unique = true, length = 11)
    @Pattern(
            regexp = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$",
            message = "올바른 전화번호 형식(하이픈 제외 10~11자리 숫자)이 아닙니다."
    )
    private String phoneNumber;

    @Column(nullable = false)
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    // 임시 추가
    private Long point;

    @Builder
    public User(String name, String email, String password, String phoneNumber, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = (role != null) ? role : UserRole.USER;
        this.point = 0L;
    }
}