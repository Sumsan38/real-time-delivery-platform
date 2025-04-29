package com.som.deliveryplatform.domain.order.controller;

import com.som.deliveryplatform.domain.order.constant.OrderApiKey;
import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.message.OrderMessagePayload;
import com.som.deliveryplatform.global.aop.annotation.Idempotent;
import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.common.ResponseDto;
import com.som.deliveryplatform.global.kafka.KafkaPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(OrderApiKey.ORDER_API_KEY)
public class OrderController {

    @Value("${kafka.topic.order}")
    private String topicOrder;

    private final KafkaPublisher kafkaPublisher;

    @Idempotent
    @PostMapping
    public ResponseEntity<ResponseDto<OrderResponse>> order(
            @RequestHeader("IdempotencyKey") String idempotencyKey,
            @RequestBody OrderRequest orderRequest) {
        OrderMessagePayload payload = new OrderMessagePayload(idempotencyKey, orderRequest);

        kafkaPublisher.publish(topicOrder, payload);

        return ResponseEntity.ok(ResponseDto.of(ResponseCode.SUCCESS, null));
    }
}
