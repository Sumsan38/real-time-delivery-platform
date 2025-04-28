package com.som.deliveryplatform.global.kafka;

import com.som.deliveryplatform.global.kafka.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumerListener {

    private final List<MessageHandler> messageHandlers;

    @KafkaListener(topics = "${kafka.topic.default}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        log.info("Received kafka message: {}", message);

        try {
            for (MessageHandler handler : messageHandlers) {
                if(handler.canHandle(message)) {
                    handler.handle(message);
                    return;
                }
            }
            log.warn("No handler found for message: {}", message);
        } catch (Exception e) {
            log.error("failed to process kafka message", e);
        }
    }
}
