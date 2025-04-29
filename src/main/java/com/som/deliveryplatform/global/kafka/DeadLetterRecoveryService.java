package com.som.deliveryplatform.global.kafka;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeadLetterRecoveryService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Counter dlqRecoveryCounter;

    // TODO 분석
    public DeadLetterRecoveryService(KafkaTemplate<String, String> kafkaTemplate, MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.dlqRecoveryCounter = Counter
                .builder("customer.dlt.recovered.count")
                .description("DLQ 복구 횟수")
                .register(meterRegistry);
    }

    /**
     * 실패 메세지를 원래 topic으로 복구 발송한다.
     * @param originalTopic 복구할 대상 원래 토픽명(ex, order-topic)
     * @param message 복구할 메세지(raw JSON)
     */
    public void recover(String originalTopic, String message) {

        try {
            kafkaTemplate.send(originalTopic, message);
            dlqRecoveryCounter.increment();
            log.info("Dead letter message successfully resend to topic: {}", originalTopic);
        } catch (Exception e) {
            throw new RuntimeException("Dead letter recovery failed", e);
        }
    }
}
