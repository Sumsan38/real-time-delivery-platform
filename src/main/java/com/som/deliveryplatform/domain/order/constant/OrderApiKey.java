package com.som.deliveryplatform.domain.order.constant;

public class OrderApiKey {

    private OrderApiKey() {
        throw new IllegalStateException("Utility class");
    }

    public static final String ORDER_API_KEY = "/api/order";
    public static final String ORDER_API_KEY_WITH_ID = "/api/order/{id}";
}
