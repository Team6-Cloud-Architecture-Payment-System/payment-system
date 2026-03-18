package com.example.paymentsystem.domain.product.entity;

import com.example.paymentsystem.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name="product")
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

    @Column(nullable = false)
    private ProductStatus status;

    @Column(nullable = false)
    private String category;

    public Product (Long price, String name, Long stock, String detail, ProductStatus status, String category) {
        this.price = price;
        this.name = name;
        this.stock = stock;
        this.detail = detail;
        this.status = status;
        this.category = category;
    }
}
