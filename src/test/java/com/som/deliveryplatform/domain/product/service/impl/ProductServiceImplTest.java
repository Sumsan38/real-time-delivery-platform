package com.som.deliveryplatform.domain.product.service.impl;

import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    public ProductServiceImplTest() {
        MockitoAnnotations.openMocks(this); // mock 초기화
    }

    @Test
    @DisplayName("상품 목록을 반환한다")
    void shouldReturnProductList() {
        // given
        Product product1 = Product.builder().id(1L).name("product1").stock(10).build();
        Product product2 = Product.builder().id(2L).name("product2").stock(20).build();
        List<Product> products = List.of(product1,product2);
        when(productRepository.findAll()).thenReturn(products);

        // when
        List<ProductResponse> result = productService.findAll();

        // then
        verify(productRepository, times(1)).findAll();
        assertThat(result).hasSize(2);
        assertThat(result.stream().findFirst().get().name()).isEqualTo(product1.getName());
    }
}