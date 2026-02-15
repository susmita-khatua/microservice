package com.ecommerce.order.client;

import com.ecommerce.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InventoryClientFallback implements InventoryClient {

    @Override
    public ApiResponse<Boolean> isInStock(String skuCode, int quantity) {
        log.warn("Fallback triggered for isInStock: {}", skuCode);
        return ApiResponse.error("Inventory service is currently unavailable");
    }

    @Override
    public ApiResponse<Object> reserveStock(StockRequest request) {
        log.warn("Fallback triggered for reserveStock: {}", request.skuCode());
        return ApiResponse.error("Inventory service is currently unavailable");
    }

    @Override
    public ApiResponse<Object> releaseStock(StockRequest request) {
        log.warn("Fallback triggered for releaseStock: {}", request.skuCode());
        return ApiResponse.error("Inventory service is currently unavailable");
    }
}
