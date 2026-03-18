package com.example.paymentsystem.domain.product.service;

import com.example.paymentsystem.domain.product.dto.response.ProductDetailResponse;
import com.example.paymentsystem.domain.product.dto.response.ProductListResponse;
import com.example.paymentsystem.domain.product.entity.Product;
import com.example.paymentsystem.domain.product.entity.ProductStatus;
import com.example.paymentsystem.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    //전체 상품 조회
    public List<ProductListResponse> getProducts(){
        return productRepository.findAll().stream()
                .map(ProductListResponse::from)
                .toList();
    }
    //단건 상품 조회
    public ProductDetailResponse getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        return ProductDetailResponse.from(product);
    }
}
