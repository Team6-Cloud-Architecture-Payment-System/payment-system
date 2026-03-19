package com.example.paymentsystem.domain.auth.entity;

import com.example.paymentsystem.common.entity.BaseEntity;
import com.example.paymentsystem.domain.membershipTier.entity.MembershipTier;
import com.example.paymentsystem.domain.point.entity.Point;
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

    @Column(nullable = false)
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "point_id")
    private Point point;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id")
    private MembershipTier membership;

    @Builder
    public User(String name, String email, String password, Point point, MembershipTier membership) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.point = point;
        this.membership = membership;
    }
}