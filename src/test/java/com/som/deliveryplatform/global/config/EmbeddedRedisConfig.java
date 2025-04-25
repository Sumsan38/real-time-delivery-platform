package com.som.deliveryplatform.global.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig implements DisposableBean {

    private RedisServer redisServer;

    @Value("${spring.data.redis.port}")
    private int port;

    @PostConstruct
    public void startRedis() {
        if(redisServer == null || ! redisServer.isActive()) {
            redisServer = new RedisServer(port); // port는 application-test.yml과 맞추기
            redisServer.start();
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            try {
                redisServer.stop();
            } catch (Exception e) {
                // 종료 시 발생하는 예외는 로깅만 하고 진행
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        stopRedis();
    }
}