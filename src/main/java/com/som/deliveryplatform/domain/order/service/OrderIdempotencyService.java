package com.som.deliveryplatform.domain.order.service;

import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;

public interface OrderIdempotencyService {
    boolean isDuplicateRequest(String idempotencyKey);
    void saveResponse(String idempotencyKey, Object response);
    OrderResponse getSavedResponse(String idempotencyKey);
}
