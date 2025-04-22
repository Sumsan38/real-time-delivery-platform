package com.som.deliveryplatform.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.service.OrderService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }
    
    @Test
    @DisplayName("주문 생성 요청이 오면 status = CREATED 를 반환한다")
    void shouldCreateOrderAndReturnStatusCreated() throws Exception {
        // given
        OrderRequest orderRequest = new OrderRequest();
        OrderResponse orderResponse = new OrderResponse();
        when(orderService.createOrder(orderRequest)).thenReturn(orderResponse);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.orderId").exists());
    }
}
