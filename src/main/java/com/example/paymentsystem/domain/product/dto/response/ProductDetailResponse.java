package com.example.paymentsystem.domain.product.dto.response;

import com.example.paymentsystem.domain.product.entity.Product;
import com.example.paymentsystem.domain.product.entity.ProductStatus;

public record ProductDetailResponse (
    Long id,
    String name,
    Long price,
    Long stock,
    String detail,
    ProductStatus status,
    String category
) {
    public static ProductDetailResponse from(Product product){
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getDetail(),
                product.getStatus(),
                product.getCategory()
        );
    }
}
