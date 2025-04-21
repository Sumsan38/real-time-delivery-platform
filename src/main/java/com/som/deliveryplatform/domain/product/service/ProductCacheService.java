package com.som.deliveryplatform.domain.product.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.global.config.RedisTtlProperties;
import com.som.deliveryplatform.global.util.redis.RedisCacheTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    public static final String PRODUCT_LIST_CACHE_KEY = "product:list";
    public static final String PRODUCT_DETAIL_CACHE_KEY = "product:detail";

    private final RedisCacheTemplate redisCacheTemplate;
    private final RedisTtlProperties ttlProperties;

    public List<ProductResponse> getCachedProductList() {
        return redisCacheTemplate.get(PRODUCT_LIST_CACHE_KEY, new TypeReference<>() {});
    }

    public void setCachedProductList(List<ProductResponse> productList) {
        redisCacheTemplate.set(PRODUCT_LIST_CACHE_KEY, productList, ttlProperties.productListTtl());
    }

    public void evictProductList() {
        redisCacheTemplate.delete(PRODUCT_LIST_CACHE_KEY);
    }

    public ProductResponse getCachedProductDetail(Long id) {
        return redisCacheTemplate.get(PRODUCT_DETAIL_CACHE_KEY + ":" + id, new TypeReference<>() {});
    }

    public void setCachedProductDetail(Long id, ProductResponse productResponse) {
        redisCacheTemplate.set(PRODUCT_DETAIL_CACHE_KEY + ":" + id, productResponse, ttlProperties.productDetailTtl());
    }

    public void evictProductDetail(Long id) {
        redisCacheTemplate.delete(PRODUCT_DETAIL_CACHE_KEY + ":" + id);
    }
}
