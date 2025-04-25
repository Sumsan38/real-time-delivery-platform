package com.som.deliveryplatform.domain.order.repository;

import com.som.deliveryplatform.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
