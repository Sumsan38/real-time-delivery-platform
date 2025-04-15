package com.som.deliveryplatform.global.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseDto<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ResponseDto<T> of(ResponseCode responseCode, T data) {
        return new ResponseDto<>(responseCode.getCode(), responseCode.getMessage(), data);
    }

    public static <T> ResponseDto<T> of(ResponseCode responseCode) {
        return new ResponseDto<>(responseCode.getCode(), responseCode.getMessage(), null);
    }
}
