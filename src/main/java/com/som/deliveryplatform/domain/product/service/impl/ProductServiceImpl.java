package com.som.deliveryplatform.domain.product.service.impl;

import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import com.som.deliveryplatform.domain.product.service.ProductCacheService;
import com.som.deliveryplatform.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductCacheService productCacheService;
    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> findAll() {
        List<ProductResponse> cached = productCacheService.getCachedProductList();
        if(cached != null) {
            return cached;
        }

        List<ProductResponse> products = productRepository.findAll().stream().map(ProductResponse::of).toList();
        productCacheService.setCachedProductList(products);

        return products;
    }
}
