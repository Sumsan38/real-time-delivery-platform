package com.som.deliveryplatform.domain.order.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.order.message.OrderMessagePayload;
import com.som.deliveryplatform.domain.order.service.OrderService;
import com.som.deliveryplatform.global.kafka.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessageHandler implements MessageHandler {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean canHandle(String rawMessage) {
        try {
            objectMapper.readValue(rawMessage, OrderMessagePayload.class);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    @Override
    public void handle(String rawMessage) {
        try {
            OrderMessagePayload payload = objectMapper.readValue(rawMessage, OrderMessagePayload.class);
            orderService.createOrder(payload.getIdempotencyKey(), payload.getOrderRequest());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("OrderMessageHandler failed to process message", e);
        }

    }
}
