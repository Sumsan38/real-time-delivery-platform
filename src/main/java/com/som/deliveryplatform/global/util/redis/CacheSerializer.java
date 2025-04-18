package com.som.deliveryplatform.global.util.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheSerializer {
    // TODO: 추후 exception 변경

    private final ObjectMapper objectMapper;

    public <T> String serialize(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("fail to serialize data.", e);
        }
    }

    public <T> T deserialize(String data, TypeReference<T> typeRef) {
        try {
            return objectMapper.readValue(data, typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("fail to deserialize data.", e);
        }
    }
}
