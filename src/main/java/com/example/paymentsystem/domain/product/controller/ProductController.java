package com.example.paymentsystem.domain.product.controller;

import com.example.paymentsystem.common.dto.ApiResponse;
import com.example.paymentsystem.domain.product.dto.response.ProductDetailResponse;
import com.example.paymentsystem.domain.product.dto.response.ProductListResponse;
import com.example.paymentsystem.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    //상품 전체목록 조회
    //GET /api/products

    @GetMapping
    public ResponseEntity<ApiResponse> getProducts() {
        List<ProductListResponse> products = productService.getProducts();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(products));
    }

    //상품 단건 조회
    //GET /api/products/{productId}
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable Long productId) {
        ProductDetailResponse product = productService.getProductById(productId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(product));
    }
}
