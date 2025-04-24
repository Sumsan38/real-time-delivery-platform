package com.som.deliveryplatform.global.aop.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.common.contenttype.ContentType;
import com.som.deliveryplatform.global.aop.annotation.Idempotent;
import com.som.deliveryplatform.global.aop.store.IdempotencyStore;
import com.som.deliveryplatform.global.aop.store.IdempotencyStoreRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private final IdempotencyStoreRegistry idempotencyStoreRegistry;

    private final Map<String, HttpStatus> statusOfRequestMethod =
            Map.of("POST", HttpStatus.CREATED, "PUT", HttpStatus.OK);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if( !handlerHasIdempotentAnnotation(handler) ) return true;

        String key = request.getHeader("IdempotencyKey");
        if(key == null || key.isBlank()) {
            response.sendError(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write(serializeToJson("idempotency key is required."));
            return false;
        }

        IdempotencyStore store = idempotencyStoreRegistry.getStore(request);
        if(store.isDuplicateRequest(key)) {
            Object cachedResponse = store.getSavedResponse(key);
            response.setStatus(statusOfRequestMethod.get(request.getMethod()).value());
            response.setContentType(ContentType.APPLICATION_JSON.getType());
            response.getWriter().write(serializeToJson(cachedResponse));
            return false;
        }

        return true;
    }

    public boolean handlerHasIdempotentAnnotation(Object handler) {
        if(! (handler instanceof HandlerMethod method)) return false;
        return method.hasMethodAnnotation(Idempotent.class);
    }

    private String serializeToJson(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("fail to serialize data.", e);
        }
    }
}
