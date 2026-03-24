package com.example.paymentsystem.common.init;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.dto.request.SignUpRequest;
import com.example.paymentsystem.domain.auth.entity.User;
import com.example.paymentsystem.domain.auth.repository.UserRepository;
import com.example.paymentsystem.domain.auth.service.AuthService;
import com.example.paymentsystem.domain.membership.entity.MembershipName;
import com.example.paymentsystem.domain.membership.entity.MembershipTier;
import com.example.paymentsystem.domain.membership.entity.UserMembership;
import com.example.paymentsystem.domain.membership.repository.MembershipTierRepository;
import com.example.paymentsystem.domain.membership.repository.UserMembershipRepository;
import com.example.paymentsystem.domain.product.entity.Product;
import com.example.paymentsystem.domain.product.entity.ProductStatus;
import com.example.paymentsystem.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInit implements ApplicationRunner {

    private final AuthService authService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MembershipTierRepository  membershipTierRepository;
    private final UserMembershipRepository userMembershipRepository;

    @Override
    public void run(ApplicationArguments args) {

        // 1. 등급 정책 데이터 먼저 주입
        if (membershipTierRepository.count() == 0) {
            membershipTierRepository.saveAll(List.of(
                    new MembershipTier(0.01, "기본", MembershipName.NORMAL, 0L, 100000L),
                    new MembershipTier(0.03, "우수", MembershipName.VIP, 100000L, 300000L),
                    new MembershipTier(0.05, "최우수", MembershipName.VVIP, 300000L, null)
            ));
            log.info("멤버십 등급 정책 등록 완료");
        }
// 1. 특정 이메일("admin@test.com")로만 조회.
        String email = "admin@test.com";
        User adminUser = userRepository.findByEmail(email).orElse(null);

// 2. '그 이메일'을 쓰는 사람이 없을 때만 가입시킴
        if (adminUser == null) {
            try {
                authService.signUp(new SignUpRequest("임하은", email, "admin", "01011113333"));
                // 가입 후 다시 조회해서 객체를 확보합니다.
                adminUser = userRepository.findByEmail(email).orElseThrow();
            } catch (Exception e) {
                log.error("관리자 계정 생성 실패", e);
            }
        }

// 3. 해당 유저의 UserMembership 데이터가 없으면 생성
        if (adminUser != null && !userMembershipRepository.existsByUserId(adminUser.getId())) {
            userMembershipRepository.save(new UserMembership(adminUser));
            log.info("임시 회원용 멤버십 데이터 생성 완료");
        }
        // 프론트 로그인 페이지에 표시된 테스트 계정
        String name = "임하은";
        email = "admin@test.com";
        String password = "admin";
        String phoneNumber = "01011113333";

        try {
            authService.signUp(new SignUpRequest(name, email, password, phoneNumber));
            log.info("임시 회원 생성 완료: {}", email);
        } catch (ServiceException e) {
            // 이미 가입된 경우 서버 시작이 실패하지 않도록 무시
            if (ErrorCode.DUPLICATED_EMAIL.getMessage().equals(e.getMessage())) {
                log.info("임시 회원이 이미 존재합니다: {}", email);
                // 회원가입만 실패했을 뿐, 테스트 상품 데이터는 계속 주입한다.
            } else {
                throw e;
            }
        }

        // 테스트용 상품 데이터(강아지 간식) 6개 주입
        // 중복 데이터가 누적되지 않도록 products 테이블이 비어있을 때만 저장
        if (productRepository.count() == 0) {
            log.info("테스트 상품 데이터 6개 등록 시작");

            List<Product> products = List.of(
                    new Product(
                            1000L,
                            "치킨맛 트레이닝 간식",
                            200L,
                            "훈련에 딱 맞는 한입 사이즈 치킨맛 간식",
                            ProductStatus.FOR_SALE,
                            "강아지 간식",
                            "https://3p-image.kurly.com/files/6f268fe3-e246-47c3-a211-051c69391e95/ee27035d-5113-42d5-9d96-9d56c2f056dc.png"
                    ),
                    new Product(
                            2500L,
                            "연어 덴탈 스틱",
                            120L,
                            "구강 케어에 도움을 주는 연어 향 덴탈 스틱",
                            ProductStatus.FOR_SALE,
                            "강아지 간식",
                            "https://cdn.daisomall.co.kr/file/resize/PD/20241204/thumb/850/thEMoHiuIvggUCaC7O6h1060695_00_00thEMoHiuIvggUCaC7O6h.jpg"
                    ),
                    new Product(
                            1800L,
                            "소고기 비스킷",
                            150L,
                            "소고기 풍미의 바삭한 비스킷",
                            ProductStatus.FOR_SALE,
                            "강아지 간식",
                            "https://images.pet-friends.co.kr/storage/pet_friends/product/id/d/f/3/9/b/3/c/df39b3ca42499350de099d3ce31bbee9/10000/f6f2c286bfd6bfb293e5e9c31766b433.jpeg"
                    ),
                    new Product(
                            3200L,
                            "고구마 크런치 쿠키",
                            80L,
                            "고구마로 만든 크런치 쿠키(기호성 좋은 간식)",
                            ProductStatus.FOR_SALE,
                            "강아지 간식",
                            "https://thumbnail.coupangcdn.com/thumbnails/remote/492x492ex/image/vendor_inventory/c95b/06d741a587a2fc79879f79cffa2190e7f6417ba8c32e1c41775e722eb109.png"
                    ),
                    new Product(
                            4100L,
                            "야채&닭가슴살 리프레시 캔디",
                            70L,
                            "야채와 닭가슴살 베이스의 상쾌한 리프레시 간식",
                            ProductStatus.FOR_SALE,
                            "강아지 간식",
                            "https://thumbnail.coupangcdn.com/thumbnails/remote/300x300ex/image/vendor_inventory/ee83/e1df5cae3e73e3a4b1fdd22e4f4e3f2c40bb5374cf4540700e6f71940dc8.png"
                    ),
                    new Product(
                            5000L,
                            "오리 저키 프리미엄",
                            60L,
                            "오리 저키 프리미엄, 쫄깃한 식감의 고급 간식",
                            ProductStatus.FOR_SALE,
                            "강아지 간식",
                            "https://images.pet-friends.co.kr/storage/pet_friends/product/id/3/e/8/3/4/7/3/3e8347333c9c1927035e031f78df17d1/10000/8f52de3beda7edbdbf59429652598854.jpeg"
                    )
            );

            productRepository.saveAll(products);
            log.info("테스트 상품 데이터 6개 등록 완료");
        }
    }
}
