package com.example.paymentsystem.domain.product.entity;

import com.example.paymentsystem.common.entity.BaseEntity;
import com.example.paymentsystem.common.exception.ErrorCode;
import com.example.paymentsystem.common.exception.ServiceException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private String detail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private String category;

    private String imgUrl;

    @Builder
    public Product (Long price, String name, Long stock, String detail, ProductStatus status, String category, String imgUrl) {
        this.price = price;
        this.name = name;
        this.stock = stock;
        this.detail = detail;
        this.status = (status != null) ? status : ProductStatus.FOR_SALE;
        this.category = category;
        this.imgUrl = imgUrl;
    }

    public void removeStock(Long quantity) {
        long restStock = this.stock - quantity;
        if (restStock < 0) {
            throw new ServiceException(ErrorCode.OUT_OF_STOCK); // 재고 부족 예외
        }
        this.stock = restStock;

        if (this.stock == 0) {
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    public void addStock(Long quantity) {
        this.stock += quantity;
        if (this.status == ProductStatus.SOLD_OUT) {
            this.status = ProductStatus.FOR_SALE;
        }
    }
}
