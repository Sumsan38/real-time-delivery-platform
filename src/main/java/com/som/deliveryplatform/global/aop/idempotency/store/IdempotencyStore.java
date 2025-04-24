package com.som.deliveryplatform.global.aop.idempotency.store;

import jakarta.servlet.http.HttpServletRequest;

public interface IdempotencyStore {

    boolean supports(HttpServletRequest request);
    boolean isDuplicateRequest(String key);
    void saveResponse(String key, Object response);
    Object getSavedResponse(String key);
}
