package com.som.deliveryplatform.domain.order.service.impl;

import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.entity.Order;
import com.som.deliveryplatform.domain.order.entity.OrderItem;
import com.som.deliveryplatform.domain.order.repository.OrderRepository;
import com.som.deliveryplatform.domain.order.service.OrderIdempotencyService;
import com.som.deliveryplatform.domain.order.service.OrderService;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import com.som.deliveryplatform.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final OrderIdempotencyService idempotencyService;
    private final ProductService productService;

    @Override
    @Transactional
    public OrderResponse createOrder(String idempotencyKey, OrderRequest request) {
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

                    // 재고 감소 처리 (상품 단위 락 적용)
                    productService.decreaseStockWithLock(item.productId(), item.quantity());

                    return OrderItem.builder()
                            .productId(item.productId())
                            .quantity(item.quantity())
                            .price(product.getPrice())
                            .build();
                })
                .toList();

        Order order = Order.of(request.userId(), orderItems);
        Order saved = orderRepository.save(order);
        OrderResponse orderResponse = OrderResponse.from(saved);

        // 캐시 저장
        idempotencyService.saveResponse(idempotencyKey, orderResponse);

        return orderResponse;
    }
}
