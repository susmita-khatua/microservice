package com.ecommerce.inventory.controller;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.StockRequest;
import com.ecommerce.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{skuCode}")
    @Operation(summary = "Get stock by SKU code")
    public ResponseEntity<ApiResponse<InventoryResponse>> getStock(@PathVariable String skuCode) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getStock(skuCode)));
    }

    @PostMapping("/add")
    @Operation(summary = "Add stock for a product")
    public ResponseEntity<ApiResponse<InventoryResponse>> addStock(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock added", inventoryService.addStock(request)));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock for an order")
    public ResponseEntity<ApiResponse<InventoryResponse>> reserveStock(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock reserved", inventoryService.reserveStock(request)));
    }

    @PostMapping("/release")
    @Operation(summary = "Release reserved stock")
    public ResponseEntity<ApiResponse<InventoryResponse>> releaseStock(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock released", inventoryService.releaseStock(request)));
    }

    @PostMapping("/confirm-deduction")
    @Operation(summary = "Confirm stock deduction after successful order")
    public ResponseEntity<ApiResponse<InventoryResponse>> confirmDeduction(@Valid @RequestBody StockRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Stock deducted", inventoryService.confirmStockDeduction(request)));
    }

    @GetMapping("/check/{skuCode}")
    @Operation(summary = "Check if product is in stock")
    public ResponseEntity<ApiResponse<Boolean>> isInStock(
            @PathVariable String skuCode,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.isInStock(skuCode, quantity)));
    }

    @GetMapping("/alerts/low-stock")
    @Operation(summary = "Get low stock alerts")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStockAlerts() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getLowStockAlerts()));
    }
}
