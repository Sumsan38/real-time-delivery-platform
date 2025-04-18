package com.som.deliveryplatform.domain.product.controller;

import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.service.ProductService;
import com.som.deliveryplatform.global.common.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        // spring filter chain을 거치지 않는다.
        // 이 방식은 Spring MVC DispatcherServlet만 직접 실행
        // Spring Security FilterChain은 등록죄 않는다. (인증/인가 무시)
        // 즉, 테스트 대상은 컨트롤러 자체의 로직이며 Security 필터는 동작하지 않는다.
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    void shouldReturnProductList() throws Exception {
        // given
        List<ProductResponse> mockProducts = List.of(
                ProductResponse.of(Product.builder().id(1L).name("product1").price(1000).stock(10).build()),
                ProductResponse.of(Product.builder().id(2L).name("product2").price(2000).stock(5).build())
        );
        when(productService.findAll()).thenReturn(mockProducts);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data[0].name").value("product1"))
                .andExpect(jsonPath("$.data[1].price").value(2000))
        ;
    }
}