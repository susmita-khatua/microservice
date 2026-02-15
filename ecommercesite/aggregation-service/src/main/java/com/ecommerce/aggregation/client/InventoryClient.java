package com.ecommerce.aggregation.client;

import com.ecommerce.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/{skuCode}")
    ApiResponse<InventoryDto> getStock(@PathVariable("skuCode") String skuCode);

    record InventoryDto(
            Long id, String skuCode, Integer quantity,
            Integer reservedQuantity, Integer availableQuantity, boolean lowStock
    ) {}
}
