package com.som.deliveryplatform.global.exception;

import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.common.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseDto<Void>> handleNoSuchElementException(NoSuchElementException e) {
        log.warn("No such element exception: {}", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.of(ResponseCode.NO_SUCH_ELEMENT));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<Void>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("Validation exception: {}", e.getMessage());

        // 유효성 검사 실패시
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto.of(ResponseCode.VALIDATION_FAILED));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ResponseDto<Void>> handleException(Exception ex) {
        log.error("UnExpected server error", ex);   // print full stack trace

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDto.of(ResponseCode.INTERNAL_SERVER_ERROR));
    }
}
