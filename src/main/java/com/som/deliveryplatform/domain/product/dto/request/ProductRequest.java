package com.som.deliveryplatform.domain.product.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequest(
        @NotBlank(message = "상품명은 필수입니다.")
        String name,
        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0 이상이여야합니다.")
        Integer price,
        @NotNull(message = "재고는 필수입니다.")
        @Min(value = 0, message = "재교는 0 이상이여야합니다.")
        Integer stock) {

    public static ProductRequest of(String name, Integer price, Integer stock) {
        return new ProductRequest(name, price, stock);
    }
}
