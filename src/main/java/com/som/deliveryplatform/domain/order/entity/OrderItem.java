package com.som.deliveryplatform.domain.order.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private Long productId;

    private int quantity;
    private int price;  // 주문 당시 가격 (상품 가격과 분리)

    public void setOrder(Order order) {
        this.order = order;
    }
}
