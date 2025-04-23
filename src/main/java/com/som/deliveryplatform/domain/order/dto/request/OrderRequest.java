package com.som.deliveryplatform.domain.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "사용자 ID는 필수입니다")
        Long userId,

        @NotNull(message = "주문 상품 목록은 필수입니다.")
        List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotNull(message = "상품 ID는 필수입니다.")
            Long productId,

            @Min(value = 1, message = "최소 수량은 1 이상이어야 합니다.")
            int quantity
    ) {
    }
}
