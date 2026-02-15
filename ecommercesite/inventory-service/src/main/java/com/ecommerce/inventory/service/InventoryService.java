package com.ecommerce.inventory.service;

import com.ecommerce.common.exception.BadRequestException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.StockRequest;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryResponse getStock(String skuCode) {
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "skuCode", skuCode));
        return mapToResponse(inventory);
    }

    @Transactional
    public InventoryResponse addStock(StockRequest request) {
        Inventory inventory = inventoryRepository.findBySkuCode(request.getSkuCode())
                .orElse(Inventory.builder()
                        .skuCode(request.getSkuCode())
                        .quantity(0)
                        .reservedQuantity(0)
                        .build());

        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        Inventory saved = inventoryRepository.save(inventory);
        log.info("Stock added for SKU {}: +{} (total: {})", request.getSkuCode(), request.getQuantity(), saved.getQuantity());
        return mapToResponse(saved);
    }

    @Transactional
    public InventoryResponse reserveStock(StockRequest request) {
        Inventory inventory = inventoryRepository.findBySkuCode(request.getSkuCode())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "skuCode", request.getSkuCode()));

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock for SKU: " + request.getSkuCode()
                    + ". Available: " + inventory.getAvailableQuantity()
                    + ", Requested: " + request.getQuantity());
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.getQuantity());
        Inventory saved = inventoryRepository.save(inventory);

        log.info("Stock reserved for SKU {}: {} units", request.getSkuCode(), request.getQuantity());
        return mapToResponse(saved);
    }

    @Transactional
    public InventoryResponse releaseStock(StockRequest request) {
        Inventory inventory = inventoryRepository.findBySkuCode(request.getSkuCode())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "skuCode", request.getSkuCode()));

        int newReserved = Math.max(0, inventory.getReservedQuantity() - request.getQuantity());
        inventory.setReservedQuantity(newReserved);
        Inventory saved = inventoryRepository.save(inventory);

        log.info("Stock released for SKU {}: {} units", request.getSkuCode(), request.getQuantity());
        return mapToResponse(saved);
    }

    @Transactional
    public InventoryResponse confirmStockDeduction(StockRequest request) {
        Inventory inventory = inventoryRepository.findBySkuCode(request.getSkuCode())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "skuCode", request.getSkuCode()));

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.getQuantity());
        Inventory saved = inventoryRepository.save(inventory);

        log.info("Stock deducted for SKU {}: {} units (remaining: {})", request.getSkuCode(), request.getQuantity(), saved.getQuantity());
        return mapToResponse(saved);
    }

    public boolean isInStock(String skuCode, int quantity) {
        return inventoryRepository.findBySkuCode(skuCode)
                .map(inv -> inv.getAvailableQuantity() >= quantity)
                .orElse(false);
    }

    public List<InventoryResponse> getLowStockAlerts() {
        return inventoryRepository.findLowStockItems().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .skuCode(inventory.getSkuCode())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .availableQuantity(inventory.getAvailableQuantity())
                .lowStock(inventory.isLowStock())
                .build();
    }
}
