package com.som.deliveryplatform.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>("SUCCESS", "요청이 성공했습니다.", data);
    }

    public static <T> ResponseDto<T> error(String message) {
        return new ResponseDto<>("ERROR", message, null);
    }

    public static <T> ResponseDto<T> validationFailed(String message) {
        return new ResponseDto<>("VALIDATION FAILED", message, null);
    }
}
