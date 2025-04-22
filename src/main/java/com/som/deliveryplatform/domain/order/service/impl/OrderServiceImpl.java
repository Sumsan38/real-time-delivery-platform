package com.som.deliveryplatform.domain.order.service.impl;

import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        return null;
    }
}
