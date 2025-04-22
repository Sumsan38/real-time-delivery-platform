package com.som.deliveryplatform.domain.order.service;

import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;

public interface OrderService {

    OrderResponse createOrder(OrderRequest orderRequest);
}
