package com.som.deliveryplatform.domain.order.controller;

import com.som.deliveryplatform.domain.order.constant.OrderApiKey;
import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.dto.response.OrderResponse;
import com.som.deliveryplatform.domain.order.service.OrderService;
import com.som.deliveryplatform.global.aop.annotation.Idempotent;
import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(OrderApiKey.ORDER_API_KEY)
public class OrderController {

    private final OrderService orderService;

    @Idempotent
    @PostMapping
    public ResponseEntity<ResponseDto<OrderResponse>> order(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDto.of(ResponseCode.SUCCESS, orderResponse));
    }
}
