package com.ecommerce.order.client;

import com.ecommerce.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class ProductClientFallback implements ProductClient {

    @Override
    public ApiResponse<ProductDto> getProductBySkuCode(String skuCode) {
        log.warn("Fallback triggered for getProductBySkuCode: {}", skuCode);
        return ApiResponse.error("Product service is currently unavailable");
    }
}
