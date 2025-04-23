package com.som.deliveryplatform.domain.order.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.service.OrderIdempotencyService;
import com.som.deliveryplatform.global.config.RedisTtlProperties;
import com.som.deliveryplatform.global.util.redis.RedisCacheTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderIdempotencyServiceImpl implements OrderIdempotencyService {

    private final RedisCacheTemplate redisCacheTemplate;
    private final RedisTtlProperties redisTtlProperties;

    private static final String IDEMPOTENCY_KEY_PREFIX = "order:idempotency:";

    @Override
    public boolean isDuplicateRequest(String idempotencyKey) {
        return redisCacheTemplate.get(builderKey(idempotencyKey), new TypeReference<>() {}) != null;
    }

    @Override
    public void saveResponse(String idempotencyKey, Object response) {
        redisCacheTemplate.set(
                builderKey(idempotencyKey),
                response,
                redisTtlProperties.orderIdempotencyTtl());
    }

    @Override
    public OrderResponse getSavedResponse(String idempotencyKey) {
        return redisCacheTemplate.get(builderKey(idempotencyKey), new TypeReference<>() {});
    }

    private static String builderKey(String idempotencyKey) {
        return IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
    }
}
