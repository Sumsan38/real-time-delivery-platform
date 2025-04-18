package com.som.deliveryplatform.global.util.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisCacheTemplate {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheSerializer serializer;

    public void set(String key, Object data, Duration ttl) {
        String json = serializer.serialize(data);
        redisTemplate.opsForValue().set(key, json, ttl);
    }

    public <T> T get(String key, TypeReference<T> typeRef) {
        String json = (String) redisTemplate.opsForValue().get(key);
        return serializer.deserialize(json, typeRef);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }
}
