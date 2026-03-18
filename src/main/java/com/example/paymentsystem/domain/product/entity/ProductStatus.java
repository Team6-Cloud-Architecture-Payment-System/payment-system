package com.example.paymentsystem.domain.product.entity;

public enum ProductStatus {
    FOR_SALE("판매 중"),
    STOP_SALE("판매 중지"),
    SOLD_OUT("품절");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}
