package com.ecommerce.order.client;

import com.ecommerce.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

@FeignClient(name = "payment-service", fallback = PaymentClientFallback.class)
public interface PaymentClient {

    @PostMapping("/api/payments/initiate")
    ApiResponse<PaymentDto> initiatePayment(@RequestBody PaymentRequest request);

    record PaymentRequest(String orderId, BigDecimal amount, String method) {}
    record PaymentDto(Long id, String orderId, BigDecimal amount, String status, String transactionId) {}
}
