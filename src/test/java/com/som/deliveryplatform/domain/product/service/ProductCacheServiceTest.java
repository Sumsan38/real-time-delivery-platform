package com.som.deliveryplatform.domain.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductCacheService productCacheService;

    @Value("${redis.ttl.product-list}")
    private int ttl;

    @BeforeEach
    void setUp() {
        // RedisTemplate의 opsForValue()가 반환될 객체를 지정
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("상품 목록 저장 캐싱 성공")
    void shouldSetProductListToCache() throws JsonProcessingException {
        // given
        List<ProductResponse> responses = List.of(
                ProductResponse.of(Product.builder().id(1L).name("product1").price(1000).stock(5).build())
        );

        // when
        productCacheService.setCachedProductList(responses);

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);  // Redis 저장되는 Json 값을 캡처
        // 저장 메소드가 호출되었는지 검증
        verify(valueOperations)
                .set(eq(ProductCacheService.PRODUCT_LIST_CACHE_KEY), captor.capture(), eq(Duration.ofMinutes(ttl)));

        String cachedJson = captor.getValue();
        // 역직렬화 하여 원래 객체와 같은지 확인
        List<ProductResponse> deserialized = objectMapper.readValue(
                cachedJson,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ProductResponse.class)
        );

        assertThat(deserialized).hasSize(1);
        assertThat(deserialized.get(0).name()).isEqualTo("product1");
    }

    @Test
    @DisplayName("상품 목록 캐시 조회 성공")
    void shouldGetProductListToCache() throws JsonProcessingException {
        // given
        List<ProductResponse> responses = List.of(
                ProductResponse.of(Product.builder().id(1L).name("product1").price(1000).stock(5).build())
        );
        String json = objectMapper.writeValueAsString(responses);   // 미리 직렬화된 캐시 데이터 준비
        when(valueOperations.get(ProductCacheService.PRODUCT_LIST_CACHE_KEY)).thenReturn(json); // Redis에서 읽어올 값을 지정

        // when
        List<ProductResponse> result = productCacheService.getCachedProductList();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("product1");
    }

    @Test
    @DisplayName("상품 목록 캐시 저장시 TTL이 설정되어야 한다")
    void shouldSetTTLWhenGetProductList() {
        // given
        List<ProductResponse> responses = List.of(
                ProductResponse.of(Product.builder().id(1L).name("product1").price(1000).stock(5).build())
        );

        // when
        productCacheService.setCachedProductList(responses);

        // then
        verify(valueOperations)
                .set(eq(ProductCacheService.PRODUCT_LIST_CACHE_KEY), any(String.class), eq(Duration.ofMinutes(ttl)));
    }

}