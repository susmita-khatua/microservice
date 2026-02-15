package com.ecommerce.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private Long id;
    private String skuCode;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private boolean lowStock;
}
