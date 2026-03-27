package com.example.paymentsystem.domain.product.dto.response;

import com.example.paymentsystem.domain.product.entity.Product;
import com.example.paymentsystem.domain.product.entity.ProductStatus;

public record ProductListResponse(
        Long id,
        String name,
        Long price,
        Long stock,
        ProductStatus status,
        String category,
        String imgUrl
) {
    public static ProductListResponse from(Product product) {
        return new ProductListResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCategory(),
                product.getImgUrl()
        );
    }
}
