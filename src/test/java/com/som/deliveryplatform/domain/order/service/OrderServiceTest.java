package com.som.deliveryplatform.domain.order.service;

import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.entity.Order;
import com.som.deliveryplatform.domain.order.entity.OrderItem;
import com.som.deliveryplatform.domain.order.repository.OrderRepository;
import com.som.deliveryplatform.domain.order.service.impl.OrderServiceImpl;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("주문 생성 성공 시 OrderResponse를 반환다")
    void shouldReturnOrderResponseWhenOrderCreated() {
        // given
        OrderRequest orderRequest = new OrderRequest(1L,
                List.of(new OrderRequest.OrderItemRequest(100L, 2)));
        Product product = Product.builder().id(100L).name("product").price(5000).stock(10).build();
        when(productRepository.findAllById(List.of(100L))).thenReturn(List.of(product));
        Order order = Order.of(1L, List.of(
                OrderItem.builder()
                        .productId(100L)
                        .price(5000)
                        .quantity(2)
                        .build()
        ));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // when
        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        // then
        assertThat(orderResponse).isNotNull();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

}