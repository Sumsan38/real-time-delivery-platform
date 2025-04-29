package com.som.deliveryplatform.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.order.constant.OrderApiKey;
import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.entity.Order;
import com.som.deliveryplatform.domain.order.entity.OrderItem;
import com.som.deliveryplatform.domain.order.entity.OrderStatus;
import com.som.deliveryplatform.domain.order.message.OrderMessagePayload;
import com.som.deliveryplatform.domain.order.service.OrderIdempotencyService;
import com.som.deliveryplatform.domain.order.service.OrderService;
import com.som.deliveryplatform.global.aop.idempotency.IdempotencyInterceptor;
import com.som.deliveryplatform.global.aop.idempotency.store.IdempotencyStore;
import com.som.deliveryplatform.global.aop.idempotency.store.IdempotencyStoreRegistry;
import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.common.ResponseDto;
import com.som.deliveryplatform.global.exception.GlobalExceptionHandler;
import com.som.deliveryplatform.global.kafka.KafkaPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    @Mock
    private KafkaPublisher kafkaPublisher;

    @Mock
    private OrderIdempotencyService orderIdempotencyService;

    @Mock
    private IdempotencyStoreRegistry idempotencyStoreRegistry;

    @Mock
    private IdempotencyStore idempotencyStore;

    @InjectMocks
    private OrderController orderController;

    private IdempotencyInterceptor idempotencyInterceptor;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        idempotencyInterceptor = new IdempotencyInterceptor(objectMapper, idempotencyStoreRegistry);

        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .addInterceptors(idempotencyInterceptor)
                .build();

        when(idempotencyStoreRegistry.getStore(any())).thenReturn(idempotencyStore);
        when(idempotencyStore.supports(any())).thenReturn(true);
    }

    @Test
    @DisplayName("최초 요청 시 OrderService는 호출된다")
    void shouldCallServiceOnFirstRequest() throws Exception {
        // given
        String idempotencyKey = "test-idempotencyKey-1234";
        OrderRequest request = new OrderRequest(1L, List.of(new OrderRequest.OrderItemRequest(100L, 2)));
        OrderResponse response = OrderResponse.from(new Order(1L, 1L, List.of(
                OrderItem.builder().id(1L).productId(100L).price(5000).quantity(2).build()
        ), OrderStatus.CREATED));

        when(idempotencyStore.isDuplicateRequest(idempotencyKey)).thenReturn(false);
        when(orderService.createOrder(eq(idempotencyKey), any(OrderRequest.class))).thenReturn(response);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(OrderApiKey.ORDER_API_KEY)
                        .header("IdempotencyKey", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        // then
        verify(kafkaPublisher, times(1)).publish(any(), any(OrderMessagePayload.class));
    }

    @Test
    @DisplayName("중복 요청 시 Controller가 호출되지 않는다")
    void shouldReturnCachedOnDuplicateRequest() throws Exception {
        // given
        String idempotencyKey = "test-idempotencyKey-1234";
        OrderRequest request = new OrderRequest(1L, List.of(new OrderRequest.OrderItemRequest(100L, 2)));
        OrderResponse cached = OrderResponse.from(new Order(1L, 1L, List.of(
                OrderItem.builder().id(1L).productId(100L).price(5000).quantity(2).build()
        ), OrderStatus.CREATED));

        when(idempotencyStore.isDuplicateRequest(idempotencyKey)).thenReturn(true);
        when(idempotencyStore.getSavedResponse(idempotencyKey))
                .thenReturn(ResponseDto.of(ResponseCode.SUCCESS, cached));

        // when
        mockMvc.perform(MockMvcRequestBuilders.post(OrderApiKey.ORDER_API_KEY)
                        .header("IdempotencyKey", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        // then
        verify(kafkaPublisher, never()).publish(any(), any());
    }
}
