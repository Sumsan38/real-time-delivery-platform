package com.som.deliveryplatform.domain.product.service.impl;

import com.som.deliveryplatform.domain.product.dto.request.ProductRequest;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import com.som.deliveryplatform.domain.product.service.ProductCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductCacheService productCacheService;

    public ProductServiceImplTest() {
        MockitoAnnotations.openMocks(this); // mock 초기화
    }

    @Test
    @DisplayName("상품 목록을 반환한다")
    void shouldReturnProductList() {
        // given
        Product product1 = Product.builder().id(1L).name("product1").stock(10).build();
        Product product2 = Product.builder().id(2L).name("product2").stock(20).build();
        List<Product> products = List.of(product1, product2);
        when(productRepository.findAll()).thenReturn(products);
        // cached miss 설정
        when(productCacheService.getCachedProductList()).thenReturn(null);

        // when
        List<ProductResponse> result = productService.findAll();

        // then
        verify(productRepository, times(1)).findAll();
        assertThat(result).hasSize(2);
        assertThat(result.stream().findFirst().get().name()).isEqualTo(product1.getName());
    }


    @Test
    @DisplayName("캐시가 존재할 경우 DB 조회 없이 캐시 데이터 반환")
    void shouldReturnProductListFromCache() {
        // given
        List<ProductResponse> cached = List.of(
                ProductResponse.of(Product.builder().id(1L).name("cachedProduct").price(1000).stock(5).build())
        );
        when(productCacheService.getCachedProductList()).thenReturn(cached);

        // when
        List<ProductResponse> result = productService.findAll();

        // then
        verify(productCacheService).getCachedProductList();
        verify(productRepository, never()).findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("cachedProduct");
    }

    @Test
    @DisplayName("캐시에 상품 상세가 없을 경우 DB 조회 후 캐시에 저장")
    void shouldReturnProductDetail() {
        // given
        Product product = Product.builder().id(1L).name("cachedProduct").price(1000).stock(5).build();
        ProductResponse response = ProductResponse.of(product);
        when(productCacheService.getCachedProductDetail(1L)).thenReturn(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        ProductResponse productResponse = productService.findById(1L);

        // then
        verify(productCacheService, times(1)).setCachedProductDetail(1L, response);
        verify(productRepository, times(1)).findById(1L);
        assertThat(productResponse.name()).isEqualTo(response.name());
        assertThat(productResponse.price()).isEqualTo(response.price());
    }


    @Test
    @DisplayName("캐시에 상품 상세가 존재할 경우 캐시 데이터 반환")
    void shouldReturnProductDetailFromCache() {
        // given
        ProductResponse response =
                ProductResponse.of(Product.builder().id(1L).name("cachedProduct").price(1000).stock(5).build());
        when(productCacheService.getCachedProductDetail(1L)).thenReturn(response);

        // when
        ProductResponse productResponse = productService.findById(1L);

        // then
        verify(productCacheService).getCachedProductDetail(1L);
        verify(productRepository, never()).findById(1L);
        assertThat(productResponse.name()).isEqualTo(response.name());
    }

    @Test
    @DisplayName("캐시에 없고 DB에도 없을 경우 예외 발생")
    void shouldThrownWhenProductDetailNotFound() {
        // given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.findById(1L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품 저장 시 캐시(product:list)를 초기화 한다")
    void shouldEvictProductListCacheOnSave() {
        // given
        Product product = Product.builder().id(1L).name("product1").price(1000).stock(10).build();
        when(productRepository.save(any())).thenReturn(product);

        // when
        productService.save(ProductRequest.of("product1", 1000, 10));

        // then
        verify(productCacheService, times(1)).evictProductList();
        verify(productRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("상품 수정 성공 시 캐시를 초기화한다")
    void shouldEvictProductListAndDetailCacheOnUpdate() {
        // given
        Long id = 1L;
        ProductRequest updateProduct = ProductRequest.of("updateProduct", 2000, 15);
        Product existing = Product.builder().id(id).name("oldProduct").price(1000).stock(10).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        ProductResponse result = productService.update(id, updateProduct);

        // then
        assertThat(result.name()).isEqualTo(updateProduct.name());

        verify(productCacheService, times(1)).evictProductList();
        verify(productCacheService, times(1)).evictProductDetail(id);
        verify(productRepository, times(1)).findById(id);
    }
    
    @Test
    @DisplayName("상품 삭제 성공 시 캐시를 삭제한다")
    void shouldEvictProductListAndDetailCacheOnDelete() {
        // given
        Long id = 1L;
        Product product = Product.builder().id(id).name("product1").price(1000).stock(10).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // when
        productService.delete(id);
        
        // then
        verify(productCacheService, times(1)).evictProductList();
        verify(productCacheService, times(1)).evictProductDetail(id);
        verify(productRepository, times(1)).findById(id);
        verify(productRepository, times(1)).delete(product);
    }

}