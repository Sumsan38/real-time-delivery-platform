package com.som.deliveryplatform.domain.order.repository;

import com.som.deliveryplatform.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
