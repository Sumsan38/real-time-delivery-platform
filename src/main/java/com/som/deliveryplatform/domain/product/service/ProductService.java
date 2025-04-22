package com.som.deliveryplatform.domain.product.service;

import com.som.deliveryplatform.domain.product.dto.request.ProductRequest;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> findAll();

    ProductResponse findById(Long id);

    ProductResponse save(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}
