package com.ecommerce.order.client;

import com.ecommerce.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service", fallback = InventoryClientFallback.class)
public interface InventoryClient {

    @GetMapping("/api/inventory/check/{skuCode}")
    ApiResponse<Boolean> isInStock(@PathVariable("skuCode") String skuCode, @RequestParam("quantity") int quantity);

    @PostMapping("/api/inventory/reserve")
    ApiResponse<Object> reserveStock(@RequestBody StockRequest request);

    @PostMapping("/api/inventory/release")
    ApiResponse<Object> releaseStock(@RequestBody StockRequest request);

    record StockRequest(String skuCode, Integer quantity) {}
}
