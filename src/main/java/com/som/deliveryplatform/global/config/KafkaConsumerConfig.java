package com.som.deliveryplatform.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@EnableKafka    // KafkaListenerContainerFactory 활성화
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.listener.retry.max-attempts}")
    private int maxAttempts;

    @Value("${spring.kafka.listener.retry.backoff.interval}")
    private long backoffInterval;

    @Value("${spring.kafka.listener.retry.backoff.max-interval}")
    private long maxBackoffInterval;

    @Value("${spring.kafka.listener.retry.backoff.multiplier}")
    private double backoffMultiplier;

    // DefaultErrorHandler 해당 Handler는 Kafka 전용 에러 핸들러
    // KafkaListenerContainerFactory가 활성화 되어 있을 경우 DefaultErrorHandler를 자동으로 인식한다
    // KafkaListenerContainer가 메시지를 수신할 때 예외가 발생하면 동작한다
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        // DeadLetterPublishingRecoverer 설정
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);

        // 재시도 백오프 설정
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(maxAttempts);
        backOff.setInitialInterval(backoffInterval);
        backOff.setMaxInterval(maxBackoffInterval);
        backOff.setMultiplier(backoffMultiplier);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
