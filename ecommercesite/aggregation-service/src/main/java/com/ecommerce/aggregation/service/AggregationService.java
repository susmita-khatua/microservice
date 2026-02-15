package com.ecommerce.aggregation.service;

import com.ecommerce.aggregation.client.InventoryClient;
import com.ecommerce.aggregation.client.ProductClient;
import com.ecommerce.aggregation.dto.ProductDetailsResponse;
import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AggregationService {

    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public ProductDetailsResponse getProductDetails(Long productId) {
        log.info("Aggregating product details for ID: {}", productId);

        // Fetch product data
        ApiResponse<ProductClient.ProductDto> productResponse = productClient.getProductById(productId);
        if (!productResponse.isSuccess() || productResponse.getData() == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        ProductClient.ProductDto product = productResponse.getData();

        // Fetch inventory data
        ProductDetailsResponse.ProductDetailsResponseBuilder builder = ProductDetailsResponse.builder()
                .productId(product.id())
                .name(product.name())
                .description(product.description())
                .price(product.price())
                .skuCode(product.skuCode())
                .category(product.category())
                .imageUrl(product.imageUrl());

        try {
            ApiResponse<InventoryClient.InventoryDto> inventoryResponse = inventoryClient.getStock(product.skuCode());
            if (inventoryResponse.isSuccess() && inventoryResponse.getData() != null) {
                InventoryClient.InventoryDto inventory = inventoryResponse.getData();
                builder.totalQuantity(inventory.quantity())
                        .availableQuantity(inventory.availableQuantity())
                        .inStock(inventory.availableQuantity() > 0)
                        .lowStock(inventory.lowStock());
            }
        } catch (Exception e) {
            log.warn("Could not fetch inventory for SKU: {}. Reason: {}", product.skuCode(), e.getMessage());
            builder.totalQuantity(0).availableQuantity(0).inStock(false).lowStock(false);
        }

        return builder.build();
    }
}
