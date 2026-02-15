package com.ecommerce.order.service;

import com.ecommerce.common.dto.ApiResponse;
import com.ecommerce.common.exception.BadRequestException;
import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.PaymentClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderLineItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;

    @Transactional
    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    @Retry(name = "orderService")
    public OrderResponse createOrder(OrderRequest request, String userId) {
        log.info("Creating order for user: {}", userId);

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .userId(userId)
                .status(OrderStatus.PENDING)
                .shippingAddress(request.getShippingAddress())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest item : request.getItems()) {
            // Fetch product details
            ApiResponse<ProductClient.ProductDto> productResponse = productClient.getProductBySkuCode(item.getSkuCode());
            if (!productResponse.isSuccess() || productResponse.getData() == null) {
                throw new BadRequestException("Product not found: " + item.getSkuCode());
            }

            // Check inventory
            ApiResponse<Boolean> stockResponse = inventoryClient.isInStock(item.getSkuCode(), item.getQuantity());
            if (!stockResponse.isSuccess() || !Boolean.TRUE.equals(stockResponse.getData())) {
                throw new BadRequestException("Insufficient stock for: " + item.getSkuCode());
            }

            // Reserve stock
            inventoryClient.reserveStock(new InventoryClient.StockRequest(item.getSkuCode(), item.getQuantity()));

            BigDecimal subtotal = productResponse.getData().price().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderLineItem lineItem = OrderLineItem.builder()
                    .skuCode(item.getSkuCode())
                    .quantity(item.getQuantity())
                    .price(productResponse.getData().price())
                    .subtotal(subtotal)
                    .build();

            order.addLineItem(lineItem);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        log.info("Order created: {} with total: {}", savedOrder.getOrderNumber(), totalAmount);
        return mapToResponse(savedOrder);
    }

    public OrderResponse createOrderFallback(OrderRequest request, String userId, Throwable t) {
        log.error("Fallback triggered for createOrder. Reason: {}", t.getMessage());
        throw new BadRequestException("Order creation is temporarily unavailable. Please try again later.");
    }

    @Transactional
    public OrderResponse cancelOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));

        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel order in status: " + order.getStatus());
        }

        // Release reserved stock
        order.getLineItems().forEach(item -> {
            try {
                inventoryClient.releaseStock(new InventoryClient.StockRequest(item.getSkuCode(), item.getQuantity()));
            } catch (Exception e) {
                log.error("Failed to release stock for SKU: {}", item.getSkuCode(), e);
            }
        });

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        log.info("Order cancelled: {}", orderNumber);
        return mapToResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(String orderNumber, OrderStatus status) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        order.setStatus(status);
        Order saved = orderRepository.save(order);
        log.info("Order {} status updated to: {}", orderNumber, status);
        return mapToResponse(saved);
    }

    public OrderResponse getOrder(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .items(order.getLineItems().stream()
                        .map(item -> OrderResponse.LineItemResponse.builder()
                                .skuCode(item.getSkuCode())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .subtotal(item.getSubtotal())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
