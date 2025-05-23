package com.som.deliveryplatform.domain.product.dto.response;

import com.som.deliveryplatform.domain.product.entity.Product;

public record ProductResponse(Long id, String name, Integer price, Integer stock) {

    public static ProductResponse of(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }
}
