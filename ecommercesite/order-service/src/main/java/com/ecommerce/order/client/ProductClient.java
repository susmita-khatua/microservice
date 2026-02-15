package com.ecommerce.order.client;

import com.ecommerce.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {

    @GetMapping("/api/products/sku/{skuCode}")
    ApiResponse<ProductDto> getProductBySkuCode(@PathVariable("skuCode") String skuCode);

    record ProductDto(Long id, String name, BigDecimal price, String skuCode, String category) {}
}
