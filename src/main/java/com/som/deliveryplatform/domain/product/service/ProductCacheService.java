package com.som.deliveryplatform.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    public static final String PRODUCT_LIST_CACHE_KEY = "product:list";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.ttl}")
    private int ttl;
    private final Duration TTL = Duration.ofMinutes(ttl);

    public List<ProductResponse> getCachedProductList() {
        try {
            String json = (String) redisTemplate.opsForValue().get(PRODUCT_LIST_CACHE_KEY);
            if (json == null) return null;

            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.warn("fail to read product list from cache: {}", e.getMessage());
            return null;
        }
    }

    public void setCachedProductList(List<ProductResponse> productList) {
        try {
            String json = objectMapper.writeValueAsString(productList);

            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ops.set(PRODUCT_LIST_CACHE_KEY, json, TTL);
        } catch (JsonProcessingException e) {
            log.warn("fail to write product list to cache: {}", e.getMessage());
        }
    }

    public void evictProductList() {
        redisTemplate.delete(PRODUCT_LIST_CACHE_KEY);
    }
}
