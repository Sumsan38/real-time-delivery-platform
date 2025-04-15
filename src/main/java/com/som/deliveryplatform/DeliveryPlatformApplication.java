package com.som.deliveryplatform;

import com.som.deliveryplatform.global.auth.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class DeliveryPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeliveryPlatformApplication.class, args);
	}

}
