package com.som.deliveryplatform.domain.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.som.deliveryplatform.domain.order.constant.OrderApiKey;
import com.som.deliveryplatform.domain.order.dto.request.OrderRequest;
import com.som.deliveryplatform.domain.order.repository.OrderItemRepository;
import com.som.deliveryplatform.domain.order.repository.OrderRepository;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import com.som.deliveryplatform.domain.user.entity.User;
import com.som.deliveryplatform.domain.user.model.Role;
import com.som.deliveryplatform.domain.user.repository.UserRepository;
import com.som.deliveryplatform.global.auth.model.TokenModel;
import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.support.JwtTestHelper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderIntegrationTest {
    // 실제 로컬 redis 띄우지 않으면 실패한다 (TODO check)

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtTestHelper jwtTestHelper;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // 유저 저장
        userRepository.save(User.builder()
                .name("test")
                .email("test@test.com")
                .role(Role.USER)
                .build());
        // 샘플 상품 저장
        productRepository.save(Product.builder()
                .name("sampleProduct")
                .price(1000)
                .stock(10)
                .build());
    }

    @AfterEach
    void cleanUp() {
        orderItemRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch();
        productRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("주문 생성 통합 테스트 - 멱등키 포함")
    void createOrder_success() throws Exception {
        // given
        String idempotencyKey = "test-idempotencyKey-1234";
        Long productId = productRepository.findAll().get(0).getId();
        User user = userRepository.findAll().get(0);
        String token = jwtTestHelper.createTestToken(user.getEmail(), user.getRole());

        OrderRequest.OrderItemRequest item = new OrderRequest.OrderItemRequest(productId, 2);
        OrderRequest orderRequest = new OrderRequest(user.getId(), List.of(item));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post(OrderApiKey.ORDER_API_KEY)
                        .cookie(new Cookie(TokenModel.TOKEN_COOKIE_NAME, token))
                        .header("IdempotencyKey", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS.name()))
                .andExpect(jsonPath("$.data.orderId").exists())
        ;
    }

}
