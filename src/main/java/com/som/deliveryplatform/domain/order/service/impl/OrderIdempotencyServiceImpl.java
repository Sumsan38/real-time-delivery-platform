package com.som.deliveryplatform.domain.order.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.som.deliveryplatform.domain.order.constant.OrderApiKey;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.service.OrderIdempotencyService;
import com.som.deliveryplatform.global.aop.idempotency.store.IdempotencyStore;
import com.som.deliveryplatform.global.config.RedisTtlProperties;
import com.som.deliveryplatform.global.util.redis.RedisCacheTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderIdempotencyServiceImpl implements OrderIdempotencyService, IdempotencyStore {

    private final RedisCacheTemplate redisCacheTemplate;
    private final RedisTtlProperties redisTtlProperties;

    private static final String IDEMPOTENCY_KEY_ORDER_PREFIX = "order:idempotency:";
    private static final Set<String> IDEMPOTENT_METHODS = Set.of("POST", "PUT");

    @Override
    public boolean supports(HttpServletRequest request) {
        return IDEMPOTENT_METHODS.contains(request.getMethod().toUpperCase()) &&
                request.getRequestURI().startsWith(OrderApiKey.ORDER_API_KEY);
    }

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
        return IDEMPOTENCY_KEY_ORDER_PREFIX + idempotencyKey;
    }
}
