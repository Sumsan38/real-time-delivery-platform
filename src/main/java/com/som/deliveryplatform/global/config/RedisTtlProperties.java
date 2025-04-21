package com.som.deliveryplatform.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "redis.ttl")
public class RedisTtlProperties {

    private int productList;
    private int productDetail;

    public Duration productListTtl(){
        return Duration.ofMinutes(productList);
    }

    public Duration productDetailTtl(){
        return Duration.ofMinutes(productDetail);
    }
}
