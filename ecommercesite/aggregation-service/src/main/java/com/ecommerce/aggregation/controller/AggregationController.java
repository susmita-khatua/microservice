package com.ecommerce.aggregation.controller;

import com.ecommerce.aggregation.dto.ProductDetailsResponse;
import com.ecommerce.aggregation.service.AggregationService;
import com.ecommerce.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product-details")
@RequiredArgsConstructor
@Tag(name = "Product Details", description = "Aggregated product details API (BFF)")
public class AggregationController {

    private final AggregationService aggregationService;

    @GetMapping("/{id}")
    @Operation(summary = "Get aggregated product details",
            description = "Fetches product data and inventory data, combines them into a single optimized payload")
    public ResponseEntity<ApiResponse<ProductDetailsResponse>> getProductDetails(
            @PathVariable Long id) {
        ProductDetailsResponse response = aggregationService.getProductDetails(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
