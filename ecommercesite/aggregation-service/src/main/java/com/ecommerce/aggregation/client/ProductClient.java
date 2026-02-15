package com.ecommerce.aggregation.client;

import com.ecommerce.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ApiResponse<ProductDto> getProductById(@PathVariable("id") Long id);

    record ProductDto(
            Long id, String name, String description,
            BigDecimal price, String skuCode, String category,
            String imageUrl, boolean active,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {}
}
