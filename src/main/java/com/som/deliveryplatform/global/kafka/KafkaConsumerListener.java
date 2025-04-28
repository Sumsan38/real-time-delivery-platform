package com.som.deliveryplatform.global.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumerListener {

    @KafkaListener(topics = "${kafka.topic.default}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Received kafka message: {}", message);
    }
}
