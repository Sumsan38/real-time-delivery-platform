package com.som.deliveryplatform.domain.product.service.impl;

import com.som.deliveryplatform.domain.product.dto.request.ProductRequest;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.entity.Product;
import com.som.deliveryplatform.domain.product.repository.ProductRepository;
import com.som.deliveryplatform.domain.product.service.ProductCacheService;
import com.som.deliveryplatform.domain.product.service.ProductService;
import com.som.deliveryplatform.global.util.redis.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductCacheService productCacheService;
    private final ProductRepository productRepository;
    private final LockService lockService;

    private static final String PRODUCT_LOCK_KEY = "product:lock:";

    @Override
    public List<ProductResponse> findAll() {
        List<ProductResponse> cached = productCacheService.getCachedProductList();
        if(cached != null) {
            return cached;
        }

        List<ProductResponse> products = productRepository.findAll().stream().map(ProductResponse::of).toList();
        productCacheService.setCachedProductList(products);

        return products;
    }

    @Override
    public ProductResponse findById(Long id) {
        ProductResponse cached = productCacheService.getCachedProductDetail(id);
        if(cached != null) {
            return cached;
        }

        Product product = getProduct(id);
        ProductResponse response = ProductResponse.of(product);

        productCacheService.setCachedProductDetail(id, response);
        return response;
    }

    @Override
    public ProductResponse save(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .stock(request.stock())
                .build();

        Product saved = productRepository.save(product);

        productCacheService.evictProductList();

        return ProductResponse.of(saved);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);
        product.update(request.name(), request.price(), request.stock());

        productCacheService.evictProductList();
        productCacheService.evictProductDetail(id);

        return ProductResponse.of(product);
    }

    @Override
    public void delete(Long id) {
        Product product = getProduct(id);

        productCacheService.evictProductList();
        productCacheService.evictProductDetail(id);

        productRepository.delete(product);
    }

    @Override
    public void decreaseStockWithLock(Long id, int quantity) {
        String lockKey = PRODUCT_LOCK_KEY + id;

        lockService.executeWithLock(lockKey, 3, 10, () -> {
            Product product = getProduct(id);

            if (product.getStock() < quantity) {
                throw new IllegalArgumentException("재고 부족");
            }

            product.decreaseStock(quantity);
            productRepository.save(product);

            return null;
        });
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
