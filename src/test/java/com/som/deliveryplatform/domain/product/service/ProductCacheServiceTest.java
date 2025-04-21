package com.som.deliveryplatform.domain.product.service;

import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.global.config.RedisTtlProperties;
import com.som.deliveryplatform.global.util.redis.RedisCacheTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ProductCacheServiceTest {

    private ProductCacheService productCacheService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RedisCacheTemplate redisCacheTemplate;

    @Autowired
    private RedisTtlProperties redisTtlProperties;

    private Duration expectedTtl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        expectedTtl = Duration.ofMinutes(redisTtlProperties.getProductList());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        productCacheService = new ProductCacheService(redisCacheTemplate, redisTtlProperties);
    }

    @Test
    @DisplayName("상품 목록을 캐시에 저장한다(TTL 포함)")
    void shouldSetProductListToCache() {
        // given
        List<ProductResponse> products =
                List.of(ProductResponse.of(Product.builder().id(1L).name("product1").price(1000).stock(10).build()));

        // when
        productCacheService.setCachedProductList(products);

        // then
        verify(redisCacheTemplate).set(
                eq(ProductCacheService.PRODUCT_LIST_CACHE_KEY),
                eq(products),
                eq(expectedTtl));
    }

    @Test
    @DisplayName("캐시에 저장된 상품 목록을 조회 성공")
    void shouldGetProductListFromCache() {
        // given
        List<ProductResponse> products = List.of(
                ProductResponse.of(Product.builder().id(1L).name("product1").price(1000).stock(10).build()),
                ProductResponse.of(Product.builder().id(2L).name("product2").price(500).stock(20).build())
        );
        when(redisCacheTemplate.get(eq(ProductCacheService.PRODUCT_LIST_CACHE_KEY), any())).thenReturn(products);

        // when
        List<ProductResponse> result = productCacheService.getCachedProductList();

        // then
        assertThat(result).isEqualTo(products);
    }

    @Test
    @DisplayName("캐시에 저장된 데이터가 없으면 null 반환")
    void shouldReturnNullWhenCacheIsEmpty() {
        // given
        when(redisCacheTemplate.get(eq(ProductCacheService.PRODUCT_LIST_CACHE_KEY), any())).thenReturn(null);

        // when
        List<ProductResponse> cachedProductList = productCacheService.getCachedProductList();

        // then
        assertThat(cachedProductList).isNull();
    }
    
    @Test
    @DisplayName("캐시 삭제시 Redis 키가 제거된다")
    void shouldDeleteProductListFromCache() {
        // when
        productCacheService.evictProductList();

        // then
        verify(redisCacheTemplate).delete(ProductCacheService.PRODUCT_LIST_CACHE_KEY);
    }
}