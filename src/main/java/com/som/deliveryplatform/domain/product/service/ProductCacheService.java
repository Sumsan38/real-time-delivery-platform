package com.som.deliveryplatform.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private static final String PRODUCT_LIST_CACHE_KEY = "product:list";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public List<ProductResponse> getCachedProductList() {
        try {
            String json = (String) redisTemplate.opsForValue().get(PRODUCT_LIST_CACHE_KEY);
            if (json == null) return List.of();

            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.warn("fail to read product list from cache: {}", e.getMessage());
            return List.of();
        }
    }

    public void setCachedProductList(List<ProductResponse> productList) {
        try {
            String json = objectMapper.writeValueAsString(productList);
            redisTemplate.opsForValue().set(PRODUCT_LIST_CACHE_KEY, json);
        } catch (JsonProcessingException e) {
            log.warn("fail to write product list to cache: {}", e.getMessage());
        }
    }

    public void evictProductList() {
        redisTemplate.delete(PRODUCT_LIST_CACHE_KEY);
    }
}
