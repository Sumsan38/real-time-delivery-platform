package com.som.deliveryplatform.domain.order.service;

import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("주문 생성 성공 시 OrderResponse를 반환다")
    void shouldReturnOrderResponseWhenOrderCreated() {
        // given
        OrderRequest orderRequest = new OrderRequest(1L,
                List.of(new OrderRequest.OrderItemRequest(100L, 2)));

        // when
        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        // then
        assertThat(orderResponse).isNotNull();
    }

}