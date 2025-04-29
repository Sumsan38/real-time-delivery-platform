package com.som.deliveryplatform.global.kafka.controller;

import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.common.ResponseDto;
import com.som.deliveryplatform.global.kafka.DeadLetterRecoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/dlt")
@RequiredArgsConstructor
public class DeadLetterRecoveryController {

    private final DeadLetterRecoveryService recoveryService;

    @PostMapping("/recover")
    public ResponseEntity<ResponseDto<Void>> recover(@RequestParam String topic, @RequestBody String message) {
        log.info("Dead letter message received. topic: {}, message: {}", topic, message);
        recoveryService.recover(topic, message);

        return ResponseEntity.ok(ResponseDto.of(ResponseCode.SUCCESS, null));
    }
}
