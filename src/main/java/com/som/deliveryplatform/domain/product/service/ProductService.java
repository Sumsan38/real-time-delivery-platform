package com.som.deliveryplatform.domain.product.service;

import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll();
}
