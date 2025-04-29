package com.som.deliveryplatform.domain.order.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.order.message.OrderMessagePayload;
import com.som.deliveryplatform.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaListener {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @KafkaListener(topics = "${kafka.topic.order}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenOrder(String message) {
        log.info("Order received: {}", message);

        try {
            OrderMessagePayload payload = objectMapper.readValue(message, OrderMessagePayload.class);
            log.info("Kafka Order Message received: idempotencyKey={}, userId={}",
                    payload.getIdempotencyKey(), payload.getOrderRequest().userId());

            orderService.createOrder(payload.getIdempotencyKey(), payload.getOrderRequest());
        } catch (JsonProcessingException e) {
            log.error("fail to parse message. message: {}", message, e);
        }
    }
}
