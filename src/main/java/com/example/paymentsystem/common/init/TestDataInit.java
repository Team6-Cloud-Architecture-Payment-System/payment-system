package com.example.paymentsystem.common.init;

import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import com.example.paymentsystem.domain.auth.dto.request.SignUpRequest;
import com.example.paymentsystem.domain.auth.service.AuthService;
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

    @Override
    public void run(ApplicationArguments args) {
        // 프론트 로그인 페이지에 표시된 테스트 계정
        String name = "임하은";
        String email = "admin@test.com";
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
                            "강아지 간식"
                    ),
                    new Product(
                            2500L,
                            "연어 덴탈 스틱",
                            120L,
                            "구강 케어에 도움을 주는 연어 향 덴탈 스틱",
                            ProductStatus.FOR_SALE,
                            "강아지 간식"
                    ),
                    new Product(
                            1800L,
                            "소고기 비스킷",
                            150L,
                            "소고기 풍미의 바삭한 비스킷",
                            ProductStatus.FOR_SALE,
                            "강아지 간식"
                    ),
                    new Product(
                            3200L,
                            "고구마 크런치 쿠키",
                            80L,
                            "고구마로 만든 크런치 쿠키(기호성 좋은 간식)",
                            ProductStatus.FOR_SALE,
                            "강아지 간식"
                    ),
                    new Product(
                            4100L,
                            "야채&닭가슴살 리프레시 캔디",
                            70L,
                            "야채와 닭가슴살 베이스의 상쾌한 리프레시 간식",
                            ProductStatus.FOR_SALE,
                            "강아지 간식"
                    ),
                    new Product(
                            5000L,
                            "오리 저키 프리미엄",
                            60L,
                            "오리 저키 프리미엄, 쫄깃한 식감의 고급 간식",
                            ProductStatus.FOR_SALE,
                            "강아지 간식"
                    )
            );

            productRepository.saveAll(products);
            log.info("테스트 상품 데이터 6개 등록 완료");
        }
    }
}
