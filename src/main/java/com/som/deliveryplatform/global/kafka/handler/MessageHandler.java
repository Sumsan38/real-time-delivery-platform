package com.som.deliveryplatform.global.kafka.handler;

public interface MessageHandler {
    boolean canHandle(String rawMessage);
    void handle(String rawMessage);
}
