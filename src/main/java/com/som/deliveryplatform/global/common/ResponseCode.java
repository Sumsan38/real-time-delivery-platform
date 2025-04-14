package com.som.deliveryplatform.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    SUCCESS("SUCCESS", "요청이 성공했습니다"),
    VALIDATION_FAILED("VALIDATION_FAILED", "요청 값이 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다"),
    ;

    private final String code;
    private final String message;
}
