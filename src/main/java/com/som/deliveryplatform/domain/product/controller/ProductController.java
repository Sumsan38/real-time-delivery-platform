package com.som.deliveryplatform.domain.product.controller;

import com.som.deliveryplatform.domain.product.dto.request.ProductRequest;
import com.som.deliveryplatform.domain.product.dto.response.ProductResponse;
import com.som.deliveryplatform.domain.product.service.ProductService;
import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.common.ResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<ProductResponse>>> getProducts() {
        List<ProductResponse> productResponses = productService.findAll();

        return ResponseEntity.ok(ResponseDto.of(ResponseCode.SUCCESS, productResponses));
    }

    @PostMapping
    public ResponseEntity<ResponseDto<ProductResponse>> saveProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse productResponse = productService.save(request);

        return ResponseEntity.ok(ResponseDto.of(ResponseCode.SUCCESS, productResponse));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<ProductResponse>> getProduct(@PathVariable Long id) {
        ProductResponse productResponse = productService.findById(id);

        return ResponseEntity.ok(ResponseDto.of(ResponseCode.SUCCESS, productResponse));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<ProductResponse>> updateProduct(@PathVariable Long id,
                                                                      @Valid @RequestBody ProductRequest request) {
        ProductResponse productResponse = productService.update(id, request);

        return ResponseEntity.ok(ResponseDto.of(ResponseCode.SUCCESS, productResponse));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);

        return ResponseEntity.ok(ResponseDto.of(ResponseCode.SUCCESS, null));
    }
}
