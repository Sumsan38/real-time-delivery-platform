package com.som.deliveryplatform.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.product.dto.request.ProductRequest;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.service.ProductService;
import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        // spring filter chain을 거치지 않는다.
        // 이 방식은 Spring MVC DispatcherServlet만 직접 실행
        // Spring Security FilterChain은 등록죄 않는다. (인증/인가 무시)
        // 즉, 테스트 대상은 컨트롤러 자체의 로직이며 Security 필터는 동작하지 않는다.
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
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
                .andExpect(jsonPath("$.data[1].price").value(2000));
    }

    @Test
    @DisplayName("상품 저장 성공")
    void shouldSaveProduct() throws Exception {
        // given
        ProductRequest request = ProductRequest.of("product1", 1000, 10);
        Product product = Product.builder().id(1L).name("product1").price(1000).stock(10).build();
        ProductResponse response = ProductResponse.of(product);
        when(productService.save(request)).thenReturn(response);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.name").value("product1"))
                .andExpect(jsonPath("$.data.price").value(1000));
    }

    @Test
    @DisplayName("상품 저장 실패 - 잘못된 요청")
    void shouldSaveFailBadRequestWhenInvalidRequest() throws Exception {
        // given
        ProductRequest request = ProductRequest.of("", 1000, 10);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 수정 성공")
    void shouldUpdateProduct() throws Exception {
        // given
        long id = 1L;
        ProductRequest request = ProductRequest.of("updateProduct", 2000, 15);
        ProductResponse response = ProductResponse.of(
                Product.builder().id(id).name("updateProduct").price(2000).stock(15).build());
        when(productService.update(id, request)).thenReturn(response);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.name").value("updateProduct"))
                .andExpect(jsonPath("$.data.price").value(2000))
                .andExpect(jsonPath("$.data.stock").value(15));
    }

    @Test
    @DisplayName("상품 수정 - 유효하지 않은 요청")
    void shouldUpdateFailBadRequestWhenInvalidRequest() throws Exception {
        // given
        long id = 1L;
        ProductRequest request = ProductRequest.of("", 1000, 10);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/api/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void shouldDeleteProductSuccessfully() throws Exception {
        // given
        long id = 1L;
        doNothing().when(productService).delete(id);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()));
    }

    @Test
    @DisplayName("상품 삭제 실패 - 존재하지 않는 상품")
    void shouldDeleteProductFailWhenInvalidProductId() throws Exception {
        // given
        long id = 9999L;
        doThrow(new NoSuchElementException()).when(productService).delete(id);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.NO_SUCH_ELEMENT.name()))
                .andExpect(jsonPath("$.message").value(ResponseCode.NO_SUCH_ELEMENT.getMessage()));
    }

}