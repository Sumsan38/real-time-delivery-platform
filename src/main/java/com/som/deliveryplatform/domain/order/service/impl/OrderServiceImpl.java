package com.som.deliveryplatform.domain.order.service.impl;

import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.entity.Order;
import com.som.deliveryplatform.domain.order.entity.OrderItem;
import com.som.deliveryplatform.domain.order.repository.OrderRepository;
import com.som.deliveryplatform.domain.order.service.OrderService;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        List<Long> productIds = request.orderItems().stream().map(OrderRequest.OrderItemRequest::productId).toList();
        List<Product> products = productRepository.findAllById(productIds);

        Map<Long, Product> productMap = products
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        List<OrderItem> orderItems = request.orderItems().stream()
                .map(item -> {
                    Product product = productMap.get(item.productId());
                    if(product == null) {
                        throw new NoSuchElementException();
                    }

                    return OrderItem.builder()
                            .productId(item.productId())
                            .quantity(item.quantity())
                            .price(product.getPrice())
                            .build();
                })
                .collect(Collectors.toList());

        Order order = Order.of(request.userId(), orderItems);
        Order saved = orderRepository.save(order);

        return OrderResponse.from(saved);
    }
}
