package com.som.deliveryplatform.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.order.constant.OrderApiKey;
import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.entity.Order;
import com.som.deliveryplatform.domain.order.entity.OrderItem;
import com.som.deliveryplatform.domain.order.entity.OrderStatus;
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

import java.util.List;

import static org.mockito.Mockito.*;
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
        String idempotencyKey = "test-Key-1234";
        OrderRequest orderRequest = new OrderRequest(1L, List.of(new OrderRequest.OrderItemRequest(100L, 2)));
        OrderResponse orderResponse = OrderResponse.from(new Order(1L, 1L, List.of(
                OrderItem.builder()
                        .id(1L)
                        .productId(100L)
                        .price(5000)
                        .quantity(2)
                        .build()), OrderStatus.CREATED));
        when(orderService.createOrder(idempotencyKey, orderRequest)).thenReturn(orderResponse);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(OrderApiKey.ORDER_API_KEY)
                        .header("IdempotencyKey", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.orderId").value(1L))
                .andExpect(jsonPath("$.data.items[0].productId").value(100L))
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.items[0].price").value(5000));
    }

    @Test
    @DisplayName("동일한 주문 요청시 같은 orderId 반환")
    void shouldReturnSameResponseWhenDuplicateIdempotencyKey() throws Exception {
        // given
        String idempotencyKey = "test-Key-1234";
        OrderRequest orderRequest = new OrderRequest(1L, List.of(new OrderRequest.OrderItemRequest(100L, 2)));
        OrderResponse orderResponse = OrderResponse.from(new Order(1L, 1L, List.of(
                OrderItem.builder()
                        .id(1L)
                        .productId(100L)
                        .price(5000)
                        .quantity(2)
                        .build()), OrderStatus.CREATED));
        when(orderService.createOrder(idempotencyKey, orderRequest)).thenReturn(orderResponse);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(OrderApiKey.ORDER_API_KEY)
                        .header("IdempotencyKey", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.orderId").value(1L));

        // 동일 키로 한 번 더 요청
        mockMvc.perform(MockMvcRequestBuilders.post(OrderApiKey.ORDER_API_KEY)
                        .header("IdempotencyKey", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.orderId").value(1L));

        // verify + createOrder는 딱 한번만 실행되어야한다
        verify(orderService, times(1)).createOrder(eq(idempotencyKey), any(OrderRequest.class));
    }


}
