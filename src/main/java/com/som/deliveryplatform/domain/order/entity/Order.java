package com.som.deliveryplatform.domain.order.entity;

import com.som.deliveryplatform.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; // CREATED, PROCESSING, COMPLETED, CANCELED

    public void addOrderItem(OrderItem item){
        orderItems.add(item);
        item.setOrder(this);
    }

    public static Order of(Long userId, List<OrderItem> orderItems){
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        return order;
    }
}
