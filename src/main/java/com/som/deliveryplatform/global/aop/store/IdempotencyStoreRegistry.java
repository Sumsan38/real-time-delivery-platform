package com.som.deliveryplatform.global.aop.store;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IdempotencyStoreRegistry {

    private final List<IdempotencyStore> idempotencyStores;

    // 차후 throw Exception 부분 변경
    public IdempotencyStore getStore(HttpServletRequest request){
        return idempotencyStores.stream()
                .filter(store -> store.supports(request))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No IdempotencyStore found for request."));
    }
}
