package com.som.deliveryplatform.domain.order.entity;

public enum OrderStatus {
    CREATED,        // 주문 생성됨
    PROCESSING,     // 주문 처리중
    COMPLETED,      // 주문 완료됨
    CANCELED,       // 주문 취소됨
}
