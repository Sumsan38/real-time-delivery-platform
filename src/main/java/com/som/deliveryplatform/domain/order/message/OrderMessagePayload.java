package com.som.deliveryplatform.domain.order.message;

import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import lombok.Data;

@Data
public class OrderMessagePayload {

    private String idempotencyKey;
    private OrderRequest orderRequest;
}
