package com.ecommerce.order.client;

import com.ecommerce.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class PaymentClientFallback implements PaymentClient {

    @Override
    public ApiResponse<PaymentDto> initiatePayment(PaymentRequest request) {
        log.warn("Fallback triggered for initiatePayment for order: {}", request.orderId());
        return ApiResponse.error("Payment service is currently unavailable");
    }
}
