package com.ecommerce.aggregation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsResponse {

    // Product details
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String skuCode;
    private String category;
    private String imageUrl;

    // Inventory details
    private Integer totalQuantity;
    private Integer availableQuantity;
    private boolean inStock;
    private boolean lowStock;
}
