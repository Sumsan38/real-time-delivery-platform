package com.som.deliveryplatform.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.order.service.OrderService;
import com.som.deliveryplatform.global.exception.GlobalExceptionHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }
    
    @Test
    @DisplayName("주문 생성 요청이 오면 status = CREATED 를 반환한다")
    void shouldCreateOrderAndReturnStatusCreated() {
        // given
        
        // when
        
        // then

        Assertions.fail();
    }
}
