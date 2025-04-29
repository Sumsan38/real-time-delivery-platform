package com.som.deliveryplatform.global.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeadLetterRecoveryService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 실패 메세지를 원래 topic으로 복구 발송한다.
     * @param originalTopic 복구할 대상 원래 토픽명(ex, order-topic)
     * @param message 복구할 메세지(raw JSON)
     */
    public void recover(String originalTopic, String message) {

        try {
            kafkaTemplate.send(originalTopic, message);
            log.info("Dead letter message successfully resend to topic: {}", originalTopic);
        } catch (Exception e) {
            throw new RuntimeException("Dead letter recovery failed", e);
        }
    }
}
