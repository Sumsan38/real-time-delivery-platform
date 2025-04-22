package com.som.deliveryplatform.domain.order.controller;

import com.som.deliveryplatform.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private OrderService orderService;
}
