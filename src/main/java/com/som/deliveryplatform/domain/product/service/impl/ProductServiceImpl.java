package com.som.deliveryplatform.domain.product.service.impl;

import com.som.deliveryplatform.domain.product.dto.request.ProductRequest;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import com.som.deliveryplatform.domain.product.service.ProductCacheService;
import com.som.deliveryplatform.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

    @Override
    public ProductResponse findById(Long id) {
        ProductResponse cached = productCacheService.getCachedProductDetail(id);
        if(cached != null) {
            return cached;
        }

        Product product = getProduct(id);
        ProductResponse response = ProductResponse.of(product);

        productCacheService.setCachedProductDetail(id, response);
        return response;
    }

    @Override
    public ProductResponse save(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .stock(request.stock())
                .build();

        Product saved = productRepository.save(product);

        productCacheService.evictProductList();

        return ProductResponse.of(saved);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        product.update(request.name(), request.price(), request.stock());

        productCacheService.evictProductList();
        productCacheService.evictProductDetail(id);

        return ProductResponse.of(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 상품입니다."));
    }
}
