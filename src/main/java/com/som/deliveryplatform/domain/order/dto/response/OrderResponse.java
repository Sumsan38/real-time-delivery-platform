package com.som.deliveryplatform.domain.order.dto.response;

import com.som.deliveryplatform.domain.order.entity.Order;
import com.som.deliveryplatform.domain.order.entity.OrderItem;
import com.som.deliveryplatform.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        Long userId,
        LocalDateTime orderedAt,
        OrderStatus status,
        List<OrderItemResponse> items
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList()
        );
    }

    public record OrderItemResponse(
            Long productId,
            int quantity,
            int price) {

        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getProductId(),
                    item.getQuantity(),
                    item.getPrice());
        }
    }
}
